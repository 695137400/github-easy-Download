package ha.github.chao.dowload;

import com.alibaba.fastjson.JSON;
import ha.github.chao.LOG;
import ha.github.chao.MainFrom;
import ha.github.chao.Singleton;
import ha.github.chao.github.GithubTree;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-31-0031<br/>
 * Time: 0:16:36<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9">下载文件任务模版</span><br/>
 */
public class DownTask {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 是否运行
     */
    private boolean isRun = false;
    /**
     * 下载节点
     */
    private GithubTree tree;
    /**
     * 日志引用链
     */
    private JLabel log;
    /**
     * 刷新树引用链，创建列表加载任务使用
     */
    private JTree jtree;
    /**
     * 单例创建任务
     */
    static Singleton singleton = Singleton.getInstance();

    public synchronized void downFile(Map<String, Object> falg) throws Exception {
        LOG.debug("DownTask:\t" + JSON.toJSON(falg), "DownTask.log");
        String type = tree.getType();
        if ("directory".equals(type)) {
            List<GithubTree> nodes = tree.getChildrens();
            if (null == nodes || nodes.size() == 0) {
                TreeTask treeTask = new TreeTask(true);
                treeTask.setRun(false);
                treeTask.setUrl(tree.getUrl());
                treeTask.setRoot(tree);
                treeTask.setLog(log);
                treeTask.setTree(jtree);
                treeTask.setDown(true);
                singleton.setTreeTask(treeTask);
                //throw new Exception("列表未加载......");
            } else {
                for (GithubTree node : nodes) {
                    System.out.println(node.getUrl());
                    String ctype = node.getType();
                    if ("directory".equals(ctype)) {
                        String path = node.getFilePath();
                        File dir = new File(MainFrom.downfilePath + File.separator + path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                            log.setText("新建文件夹：" + dir.getAbsolutePath());
                        }
                        //本来应该用递归，这里用任务
                        DownTask downTask = new DownTask();
                        downTask.setRun(false);
                        downTask.setFileName(path);
                        downTask.setTree(node);
                        downTask.setLog(log);
                        downTask.setJtree(jtree);
                        singleton.setDownTask(downTask);
                    } else {
                        downFile(node.getUrl(), node.getTitle(), node.getFilePath());
                    }
                }
            }
        } else {
            downFile(tree.getUrl(), tree.getTitle(), tree.getFilePath());
        }
        boolean res = MainFrom.downTasks.remove(this);
        LOG.debug("DownTask删除任务\t" + fileName + ":\t" + res, "DownTask.log");
        falg.clear();
    }

    private void downFile(String url, String name, String path) throws Exception {
        url = getDownUrl(url);//下载地址;
        log.setText("正在下载文件：" + name);
        path = path.substring(0, path.lastIndexOf("/") + 1);
        RequestAndResponseTool.downLoadFromUrl(url, name, MainFrom.downfilePath + File.separator + path);
    }

    /**
     * 修改为可下载地址
     *
     * @param url
     * @return
     */
    private String getDownUrl(String url) {
        url = url.substring(MainFrom.github.length() + 1);
        url = MainFrom.rawUrl + url;
        url = url.replace("blob/", "");
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
