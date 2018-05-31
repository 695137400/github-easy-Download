package ha.github.chao.dowload;

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
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class TreeTask {

    boolean chiden = false;

    public TreeTask(boolean chid) {
        chiden = chid;
    }

    public TreeTask() {

    }

    private boolean isRun = false;
    private String url;
    private GithubTree root;
    private JTree tree;
    private JLabel log;
    static Singleton singleton = Singleton.getInstance();

    public void getDefaultTreeModel(Map<String,String> falg) throws Exception {

        Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);
        Document documents = page.getDoc();
        Elements elements = documents.getElementsByClass("files");
        if (elements.size() > 0) {
            root.setType("directory");
            for (Element element : elements) {
                Elements childrens = element.getElementsByClass("js-navigation-item");
                if (childrens.size() > 0) {
                    root.removeAllChildren();
                    if (null != root.getChildrens() && root.getChildrens().size() > 0) {
                        root.getChildrens().clear();
                    }
                    List<GithubTree> childerns = new ArrayList<>();
                    for (Element el : childrens) {
                        Elements tagA = el.getElementsByClass("js-navigation-open");
                        if (null == tagA.attr("id") || "".equals(tagA.attr("id"))) {
                            continue;
                        }
                        String urlA = MainFrom.github + tagA.attr("href");
                        String path = tagA.text();
                        String type = el.getElementsByTag("svg").attr("class").indexOf("directory") > 0 ? "directory" : "file";
                        GithubTree childern = new GithubTree(path);
                        childern.setUrl(urlA);
                        childern.setType(type);
                        childern.setFilePath(root.getFilePath() + "/" + path);
                        if ("directory".equals(type)) {
                            log.setText("加载地址：" + urlA);
                            childern.add(new GithubTree("...").setType("temp"));
                            root.add(childern);
                            TreeTask treeTask = new TreeTask();
                            treeTask.setRun(false);
                            treeTask.setUrl(urlA);
                            treeTask.setRoot(childern);
                            treeTask.setTree(tree);
                            treeTask.setLog(log);
                            if (chiden) {
                                singleton.setTreeTask(treeTask);
                            }
                        } else {
                            root.add(childern);
                        }
                        childerns.add(childern);
                    }
                    root.setChildrens(childerns);
                }
            }
        } else {
            root.setType("file");
        }
        tree.updateUI();
        singleton.treeKeys.remove(root.getTitle());
        boolean res = MainFrom.treeTasks.remove(this);
        System.out.println("删除任务：----------" + res);
        falg.remove(root.getTitle());
    }


    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public String getUrl() {
        return url;
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

}
