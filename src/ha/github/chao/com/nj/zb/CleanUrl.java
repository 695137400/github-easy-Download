package ha.github.chao.com.nj.zb;

import ha.github.chao.dowload.Page;
import ha.github.chao.dowload.RequestAndResponseTool;
import ha.github.chao.dowload.TreeTask;
import ha.github.chao.github.GithubTree;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-7-14-0014<br/>
 * Time: 2:05:57<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class CleanUrl {

    public static void main(String[] args) {
        String url = "http://www.js.10086.cn/2014/wap/index.jsp";
        try {
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);
            getDefaultTreeModel(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getDefaultTreeModel(Page page) {
        Document documents = page.getDoc();
        Elements elements = documents.getAllElements();
        if (elements.size() > 0) {
            for (int i = 0; i < elements.size(); i++) {
                if ("img".equals(elements.get(i).tagName())) {
                    System.out.println(elements.attr("src"));
                }
            }
        }
    }
}
