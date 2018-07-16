package ha.github.chao.github;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-29-0029<br/>
 * Time: 21:19:20<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class GithubTree extends DefaultMutableTreeNode {
    private List<GithubTree> childrens;

    private String title;
    private String type;
    private String url;
    private String filePath;

    public GithubTree(String title) {
        this.title = title;
        setUserObject(title);
    }

    public List<GithubTree> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<GithubTree> childrens) {
        this.childrens = childrens;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public GithubTree setType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
