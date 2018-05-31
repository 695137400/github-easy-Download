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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    static InfiniteProgressPanel glasspane = new InfiniteProgressPanel();
    static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private static String ip = "https://api.github.com/repos/";
    public static String github = "https://github.com/";
    public static String rawUrl = "https://raw.githubusercontent.com/";
    private static String user = "";
    private static String project = "";
    private static JFrame frame;
    public static String downfilePath;
    public static List<DownTask> downTasks = new ArrayList<>();
    public static List<TreeTask> treeTasks = new ArrayList<>();
    static Singleton singleton = Singleton.getInstance();
    public static List<Thread> downloadThreads = new ArrayList<>();

    public static void main(String[] args) {
        frame = new JFrame("github 任意下载");
        //frame.setResizable(false);
        frame.setIconImage(new ImageIcon("icon/Octocat.png").getImage());
        frame.setBounds(550, 300, 800, 500);
        frame.setContentPane(new MainFrom().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        glasspane.setBounds(100, 100, (dimension.width) / 2, (dimension.height) / 2);
        frame.setGlassPane(glasspane);
        try {
            for (int i = 0; i < 10; i++) {
                DownloadThread ds = new DownloadThread(i);
                TreeThread ts = new TreeThread(i);
                downloadThreads.add(ds);
                downloadThreads.add(ts);
                ds.start();
                ts.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MainFrom() {
        tree1.setVisible(false);
        button1.setEnabled(false);//变灰
        down.setEnabled(false);//变灰
        tree1.setEditable(false);

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
                                    System.out.println(root.getAbsolutePath());
                                    log.setText("新建文件夹：" + root.getAbsolutePath());
                                    root.mkdirs();
                                }
                                DownTask downTask = new DownTask();
                                downTask.setRun(false);
                                downTask.setFileName(path);
                                downTask.setUrlStr(selectedNode.getUrl());
                                downTask.setTree(selectedNode);
                                downTask.setLog(log);
                                downTask.setJtree(tree1);
                                downTask.setSavePath(path + File.separator + path);
                                singleton.setDownTask(downTask);
                                while (true) {
                                    Thread.sleep(5000);
                                    System.out.println("剩余下载进程------>"+downTasks.size());
                                    if (downTasks.size() == 0) {
                                        glasspane.stop();//结束动画
                                        log.setText("下载完成！");
                                        break;
                                    }
                                }
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
                            user = ut[0];
                            project = ut[1];
                            System.out.println("user：" + user);
                            System.out.println("project：" + project);

                            Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);

                            if (null != page) {
                                tree1.setVisible(true);
                                GithubTree tree = new GithubTree(project);
                                tree.setFilePath(project);
                                DefaultTreeModel model = new DefaultTreeModel(tree);
                                getDefaultTreeModel(page, tree);
                                tree1.setModel(model);
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
                                    }
                                }
                            }.start();
                        }
                    }
                }
            }
        });
        textField1.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been released. keyUp
             *
             * @param e
             */
            @Override
            public void keyReleased(KeyEvent e) {
                String url = textField1.getText();
                if (url.toLowerCase().indexOf("http") >= 0) {
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
        root.setUrl(page.getUrl());
        root.setTitle(project);
        Document documents = page.getDoc();
        Elements elements = documents.getElementsByClass("files");
        if (elements.size() > 0) {
            root.setType("directory");
            for (Element element : elements) {
                Elements childrens = element.getElementsByClass("js-navigation-item");
                if (childrens.size() > 0) {
                    List<GithubTree> childerns = new ArrayList<>();
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
                        childerns.add(childern);
                    }
                    root.setChildrens(childerns);
                }
            }
        } else {
            root.setType("file");
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    while (true){
                        Thread.sleep(5000);
                        System.out.println(treeTasks.size());
                        if (treeTasks.size() == 0) {
                            glasspane.stop();//结束动画
                            log.setText("加载完毕！");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        tree1.updateUI();
    }

    public static DownTask getDownTasks() {
        for (DownTask d : downTasks) {
            if (!d.isRun()) {
                return d;
            }
        }
        return null;
    }


    public static TreeTask getTreeTasks() {
        for (TreeTask t : treeTasks) {
            if (!t.isRun()) {
                return t;
            }
        }
        return null;
    }

}
