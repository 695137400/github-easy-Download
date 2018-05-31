package ha.github.chao.dowload;

import ha.github.chao.MainFrom;
import ha.github.chao.Singleton;
import ha.github.chao.github.GithubTree;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-31-0031<br/>
 * Time: 0:16:36<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class DownTask {
    private String urlStr = "";
    private String fileName = "";
    private String savePath = "";
    private boolean isRun = false;
    private GithubTree tree;
    private JLabel log;
    private JTree jtree;
    static Singleton singleton = Singleton.getInstance();
    public boolean downFile(Map<String,String> falg) throws Exception {
        System.out.println(tree.getTitle());
        String type = tree.getType();
        if ("directory".equals(type)) {
            List<GithubTree> childrens = tree.getChildrens();
            if (null == childrens || childrens.size() == 0) {
                isRun = false;
                TreeTask treeTask = new TreeTask(true);
                treeTask.setRun(false);
                treeTask.setUrl(tree.getUrl());
                treeTask.setRoot(tree);
                treeTask.setLog(log);
                treeTask.setTree(jtree);
                singleton.setTreeTask(treeTask);
                isRun = false;
            } else {
                for (int i = 0; i < childrens.size(); i++) {
                    GithubTree chid = childrens.get(i);
                    System.out.println(chid.getUrl());
                    String ctype = chid.getType();
                    if ("directory".equals(ctype)) {
                        String path = chid.getFilePath();
                        System.out.println(path);
                        File root = new File(MainFrom.downfilePath + File.separator + path);
                        if (!root.exists()) {
                            root.mkdirs();
                            log.setText("新建文件夹：" + root.getAbsolutePath());
                        }
                        DownTask downTask = new DownTask();
                        downTask.setRun(false);
                        downTask.setFileName(path);
                        downTask.setUrlStr(chid.getUrl());
                        downTask.setTree(chid);
                        downTask.setLog(log);
                        downTask.setJtree(jtree);
                        downTask.setSavePath(path);
                        singleton.setDownTask(downTask);
                    } else {
                        String fileUrl = chid.getUrl();
                        fileUrl = fileUrl.substring(MainFrom.github.length() + 1);
                        fileUrl = MainFrom.rawUrl + fileUrl;
                        fileUrl = fileUrl.replace("blob/", "");
                        String fileName = chid.getTitle();
                        String path = chid.getFilePath();
                        log.setText("正在下载文件：" + fileName);
                        path = path.substring(0, path.lastIndexOf("/") + 1);
                        try {
                            RequestAndResponseTool.downLoadFromUrl(fileUrl, fileName, MainFrom.downfilePath + File.separator + path);
                        } catch (IOException e) {
                            isRun = false;
                            throw e;
                        }
                    }
                    Thread.sleep(1000);
                }
                isRun = true;
            }
        } else {
            String fileUrl = tree.getUrl();
            fileUrl = fileUrl.substring(MainFrom.github.length() + 1);
            fileUrl = MainFrom.rawUrl + fileUrl;
            fileUrl = fileUrl.replace("blob/", "");
            String fileName = tree.getTitle();
            String path = tree.getFilePath();
            log.setText("正在下载文件：" + fileName);
            path = path.substring(0, path.lastIndexOf("/") + 1);
            try {
                RequestAndResponseTool.downLoadFromUrl(fileUrl, fileName, MainFrom.downfilePath + File.separator + path);
                isRun = true;
            } catch (IOException e) {
                isRun = false;
                throw e;
            }
        }
        if (isRun) {
            MainFrom.downTasks.remove(this);
        }
        falg.remove(fileName);
        singleton.tempKeys.remove(fileName);
        singleton.downKeys.remove(fileName);
        System.out.println("down siae ："+MainFrom.downTasks.size());
        return isRun;
    }


    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public void setTree(GithubTree tree) {
        this.tree = tree;
    }

    public void setLog(JLabel log) {
        this.log = log;
    }

    public void setJtree(JTree jtree) {
        this.jtree = jtree;
    }
}
