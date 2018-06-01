package ha.github.chao.dowload;

import com.alibaba.fastjson.JSON;
import ha.github.chao.LOG;
import ha.github.chao.MainFrom;
import ha.github.chao.Singleton;
import ha.github.chao.github.GithubTree;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-31-0031<br/>
 * Time: 0:26:39<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9">列表加载任务模版</span><br/>
 */
public class TreeTask {

    /**
     * 是否加载子目录
     */
    boolean chiden = false;

    /**
     * 是否已经运行
     */
    private boolean isRun = false;

    /**
     * 下载地址
     */
    private String url;

    /**
     * 父节点
     */
    private GithubTree root;

    /**
     * 刷新树引用琏
     */
    private JTree tree;

    /**
     * 日志引用链
     */
    private JLabel log;

    /**
     * 是否创建下载任务
     */
    private boolean isDown = false;

    /**
     * 单例创建任务
     */
    static Singleton singleton = Singleton.getInstance();

    public TreeTask(boolean chid) {
        chiden = chid;
    }

    public TreeTask() {

    }


    public synchronized void getDefaultTreeModel(Map<String, Object> falg) throws Exception {
        LOG.debug("TreeTask:\t" + JSON.toJSON(falg), "TreeTask.log");
        log.setText("加载地址：" + url);
        Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);
        Document documents = page.getDoc();
        Elements elements = documents.getElementsByClass("files");
        if (elements.size() > 0) {
            for (Element element : elements) {
                Elements els = element.getElementsByClass("js-navigation-item");
                if (els.size() > 0) {
                    root.removeAllChildren();
                    if (null != root.getChildrens() && root.getChildrens().size() > 0) {
                        root.getChildrens().clear();
                    }
                    List<GithubTree> root_chidens = new ArrayList<>();
                    for (Element el : els) {
                        Elements tagA = el.getElementsByClass("js-navigation-open");
                        if (null == tagA.attr("id") || "".equals(tagA.attr("id"))) {
                            continue;
                        }
                        String urlA = MainFrom.github + tagA.attr("href");
                        String path = tagA.text();
                        String type = el.getElementsByTag("svg").attr("class").indexOf("directory") > 0 ? "directory" : "file";
                        GithubTree node = new GithubTree(path);
                        node.setUrl(urlA);
                        node.setType(type);
                        node.setFilePath(root.getFilePath() + "/" + path);
                        if ("directory".equals(type)) {
                            node.add(new GithubTree("...").setType("temp"));
                            root.add(node);
                            TreeTask treeTask = new TreeTask();
                            treeTask.setRun(false);
                            treeTask.setUrl(urlA);
                            treeTask.setRoot(node);
                            treeTask.setTree(tree);
                            treeTask.setLog(log);
                            if (chiden) {
                                singleton.setTreeTask(treeTask);
                            }
                        } else {
                            root.add(node);
                        }
                        root_chidens.add(node);
                    }
                    root.setChildrens(root_chidens);
                }
            }
        }
        tree.updateUI();
        boolean res = MainFrom.treeTasks.remove(this);
        LOG.debug("TreeTask删除任务\t" + root.getTitle() + ":\t" + res, "TreeTask.log");
        if (isDown) {
            DownTask downTask = new DownTask();
            downTask.setRun(false);
            downTask.setFileName(root.getFilePath());
            downTask.setTree(root);
            downTask.setLog(log);
            downTask.setJtree(tree);
            singleton.setDownTask(downTask);
        }
        falg.clear();
    }


    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GithubTree getRoot() {
        return root;
    }

    public void setRoot(GithubTree root) {
        this.root = root;
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

    public void setLog(JLabel log) {
        this.log = log;
    }

    public void setDown(boolean down) {
        isDown = down;
    }
}
