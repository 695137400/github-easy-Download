package ha.github.chao;


import ha.github.chao.dowload.*;
import ha.github.chao.github.GithubTree;
import ha.github.chao.loding.InfiniteProgressPanel;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-29-0029<br/>
 * Time: 13:22:00<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class MainFrom {


    private JPanel panel1;
    private JTextField textField1;
    private JButton button1;
    private JTree tree1;
    private JScrollPane scrollPane1;
    private JButton down;
    private JLabel log;
    private static InfiniteProgressPanel glasspane = new InfiniteProgressPanel();
    private static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame frame;


    //  private static String ip = "https://api.github.com/repos/";
    /**
     * github 地址
     */
    public static String github = "https://github.com/";
    /**
     * github 下载文件地址
     */
    public static String rawUrl = "https://raw.githubusercontent.com/";
    /**
     * 选择的项目名称
     */
    private static String project = "";
    /**
     * 本地保存路径
     */
    public static String downfilePath;
    /**
     * 文件下载任务列表
     */
    public static List<DownTask> downTasks = new ArrayList<>();
    /**
     * 节点加载任务列表
     */
    public static List<TreeTask> treeTasks = new ArrayList<>();
    /**
     * 单例创建任务
     */
    private static Singleton singleton = Singleton.getInstance();
    /**
     * 线程列表
     */
    private static List<DownloadThread> downloadThreads = new ArrayList<>();
    private static List<TreeThread> treeThreads = new ArrayList<>();

    public static void main(String[] args) {
        frame = new JFrame("github 任意下载");
        //frame.setResizable(false);
        frame.setIconImage(new ImageIcon("icon/Octocat.png").getImage());
        frame.setBounds(550, 300, 800, 500);
        frame.setContentPane(new MainFrom().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        glasspane.setBounds(100, 100, (dimension.width) / 2, (dimension.height) / 2);
        frame.setGlassPane(glasspane);
    }

    /**
     * 线程启用
     *
     * @param count 启用数量
     * @param type  启用类型 1,，节点  2，下载  其它两个都执行
     */
    private void statrThread(int count, int type) {
        if (type == 1) {
            for (int i = 0; i < count; i++) {
                TreeThread ts = new TreeThread(i);
                treeThreads.add(ts);
                ts.start();
            }
        } else if (type == 2) {
            for (int i = 0; i < count; i++) {
                DownloadThread ds = new DownloadThread(i);
                downloadThreads.add(ds);
                ds.start();
            }
        } else {
            try {
                for (int i = 0; i < 10; i++) {
                    TreeThread ts = new TreeThread(i);
                    treeThreads.add(ts);
                    ts.start();
                    DownloadThread ds = new DownloadThread(i);
                    downloadThreads.add(ds);
                    ds.start();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        threadMonitoring();
    }

    /**
     * 停止线程
     *
     * @param type 1，节点线程  其它全停
     */
    private void stopThread(int type) {
        try {
            if (type == 1) {
                for (TreeThread thread : treeThreads) {
                    thread.close();
                }
                treeThreads.clear();
            } else {
                for (TreeThread thread : treeThreads) {
                    thread.close();
                }
                for (DownloadThread thread : downloadThreads) {
                    thread.close();
                }
                treeThreads.clear();
                downloadThreads.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean exit = false;

    /**
     * 线程监控
     */
    private void threadMonitoring() {
        exit = true;
        new Thread() {
            public void run() {
                try {
                    frame.setTitle("github 任意下载    启动线程监控");
                    while (exit) {
                        frame.setTitle("github 任意下载    剩余任务数    下载：" + downTasks.size() + "    列表：" + treeTasks.size());
                        if (downTasks.size() == 0 && treeTasks.size() == 0) {
                            frame.setTitle("github 任意下载");
                            log.setText("加载完毕！");
                            stopThread(0);
                            glasspane.stop();//结束动画
                            this.close();
                            break;
                        }
                        Thread.sleep(5000);
                    }
                    tree1.updateUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void close() {
                exit = false;
            }
        }.start();
    }

    private MainFrom() {
        tree1.setVisible(false);
        button1.setEnabled(false);//变灰
        down.setEnabled(false);//变灰
        tree1.setEditable(false);//不可双击编辑

        GithubTree root = new GithubTree("root");
        tree1.setModel(new DefaultTreeModel(root));
        EmptyBorder border = new EmptyBorder(15, 15, 15, 15);
        panel1.setBorder(border);
        down.addActionListener(new ActionListener() {
            /**
             * 开始下载
             * @param e
             */
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        GithubTree selectedNode = (GithubTree) tree1.getLastSelectedPathComponent();//返回最后选定的节点
                        JFileChooser fileChooser = new JFileChooser("D:\\");
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnVal = fileChooser.showOpenDialog(fileChooser);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            downfilePath = fileChooser.getSelectedFile().getAbsolutePath();//这个就是你选择的文件夹的路径
                            try {
                                glasspane.start();
                                String path = selectedNode.getFilePath();
                                File root = new File(downfilePath + File.separator + path);
                                if (!root.exists()) {
                                    log.setText("新建文件夹：" + root.getAbsolutePath());
                                    root.mkdirs();
                                }
                                DownTask downTask = new DownTask();
                                downTask.setRun(false);
                                downTask.setFileName(path);
                                downTask.setTree(selectedNode);
                                downTask.setLog(log);
                                downTask.setJtree(tree1);
                                singleton.setDownTask(downTask);
                                statrThread(10, 0);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                JOptionPane.showMessageDialog(null, e1, "错误提示", JOptionPane.ERROR_MESSAGE);
                                glasspane.stop();
                            }
                        }
                    }
                }.start();
            }
        });
        button1.addActionListener(new ActionListener() {
            /**
             * 查询根节点
             * @param e
             */
            public void actionPerformed(ActionEvent e) {
                ((GithubTree) tree1.getModel().getRoot()).removeAllChildren();
                ((DefaultTreeModel) tree1.getModel()).reload();
                tree1.updateUI();
                String url = textField1.getText();
                glasspane.start();//开始动画加载效果
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String t = url.substring(github.length());
                            String[] ut = t.split("/");
                            project = ut[1];
                            System.out.println("project：" + project);
                            Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);
                            if (null != page) {
                                tree1.setVisible(true);
                                GithubTree root = new GithubTree(project);
                                root.setFilePath(project);
                                DefaultTreeModel model = new DefaultTreeModel(root);
                                getDefaultTreeModel(page, root);
                                tree1.setModel(model);
                                statrThread(10, 1);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, e1, "错误提示", JOptionPane.ERROR_MESSAGE);
                            glasspane.stop();
                        }
                    }
                }.start();
            }
        });
        tree1.addMouseListener(new MouseAdapter() {
            /**
             * GO
             * @param e
             */
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) { // 点击了鼠标左键
                    tree1.updateUI();
                    down.setEnabled(true);
                    TreePath path = tree1.getSelectionPath();
                    if (path != null) {
                        if (tree1.isExpanded(path)) {
                            tree1.collapsePath(path);
                        } else {
                            tree1.expandPath(path);
                            new Thread() {
                                @Override
                                public void run() {
                                    GithubTree selectedNode = (GithubTree) tree1.getLastSelectedPathComponent();//返回最后选定的节点
                                    if ("directory".equals(selectedNode.getType()) && (null == selectedNode.getChildrens() || selectedNode.getChildrens().size() == 0)) {
                                        TreeTask treeTask = new TreeTask(true);
                                        treeTask.setRun(false);
                                        treeTask.setUrl(selectedNode.getUrl());
                                        treeTask.setRoot(selectedNode);
                                        treeTask.setLog(log);
                                        treeTask.setTree(tree1);
                                        singleton.setTreeTask(treeTask);
                                        statrThread(10, 1);
                                    }
                                }
                            }.start();
                        }
                    }
                }
            }
        });
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String url = textField1.getText();
                if (url.toLowerCase().contains("http")) {
                    button1.setEnabled(true);
                } else {
                    button1.setEnabled(false);
                }
            }
        });
    }

    /**
     * @param page
     * @param root
     * @return
     */
    private void getDefaultTreeModel(Page page, GithubTree root) {
        Document documents = page.getDoc();
        Elements elements = documents.getElementsByClass("files");
        if (elements.size() > 0) {
            root.setType("directory");
            for (Element element : elements) {
                Elements childrens = element.getElementsByClass("js-navigation-item");
                if (childrens.size() > 0) {
                    List<GithubTree> childes = new ArrayList<>();
                    for (Element el : childrens) {
                        Elements tagA = el.getElementsByClass("js-navigation-open");
                        if (null == tagA.attr("id") || "".equals(tagA.attr("id"))) {
                            continue;
                        }
                        String url = github + tagA.attr("href");
                        String path = tagA.text();
                        String type = el.getElementsByTag("svg").attr("class").indexOf("directory") > 0 ? "directory" : "file";
                        GithubTree childern = new GithubTree(path);
                        childern.setUrl(url);
                        childern.setType(type);
                        childern.setFilePath(root.getFilePath() + "/" + path);
                        if ("directory".equals(type)) {
                            childern.add(new GithubTree("...").setType("temp"));
                            TreeTask treeTask = new TreeTask();
                            treeTask.setRun(false);
                            treeTask.setUrl(url);
                            treeTask.setRoot(childern);
                            treeTask.setLog(log);
                            treeTask.setTree(tree1);
                            singleton.setTreeTask(treeTask);
                        }
                        root.add(childern);
                        childes.add(childern);
                    }
                    root.setChildrens(childes);
                }
            }
        } else {
            root.setType("file");
        }
        tree1.updateUI();
    }

}
