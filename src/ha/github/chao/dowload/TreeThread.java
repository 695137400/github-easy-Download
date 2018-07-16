package ha.github.chao.dowload;


import com.alibaba.fastjson.JSON;
import ha.github.chao.LOG;
import ha.github.chao.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-30-0030<br/>
 * Time: 23:51:50<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class TreeThread extends Thread {
    private final static Singleton singleton = Singleton.getInstance();

    //当前ID号
    public int ID;

    private boolean exit = true;

    public TreeThread(int id) {
        ID = id;
    }

    @Override
    public void run() {
        Map<String, Object> falg = new HashMap<>();
        while (exit) {
            System.out.println("TreeThread：" + ID + "\t\t" + JSON.toJSON(falg));
            if (falg.size() < 2) {
                falg.put("ID", ID);
                TreeTask treeTask = singleton.getTreeTasks();
                try {
                    if (null != treeTask) {
                        LOG.debug("线程：\t" + ID + "\t任务：\t" + treeTask.getRoot().getTitle(), "Singleton.log");
                        falg.put(treeTask.getRoot().getTitle(), "1");
                        treeTask.getDefaultTreeModel(falg);
                    }
                } catch (Exception e) {
                    treeTask.setRun(false);
                    falg.clear();
                    LOG.debug("线程：\t" + ID + "\t任务失败：\t" + treeTask.getRoot().getTitle() + "\t" + e.getMessage(), "Singleton.log");
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        exit = false;
    }
}