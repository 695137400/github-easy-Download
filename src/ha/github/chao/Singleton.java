package ha.github.chao;

import ha.github.chao.dowload.DownTask;
import ha.github.chao.dowload.TreeTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.<br/>
 * User: lizhichao<br/>
 * Date: 2018-5-31-0031<br/>
 * Time: 11:26:09<br/>
 * Author:lizhichao<br/>
 * Description: <span style="color:#63D3E9"></span><br/>
 */
public class Singleton {
    private Singleton() {
    }

    private static final Singleton single = new Singleton();
    public static Map<String, Object> downKeys = new HashMap<>();
    public static Map<String, Object> treeKeys = new HashMap<>();
    public static Map<String, Object> tempKeys = new HashMap<>();

    //静态工厂方法
    public static Singleton getInstance() {
        return single;
    }

    public static DownTask getDownTasks() {
        DownTask task =null;
        try {
            task = MainFrom.getDownTasks();
            if (null != task && null == tempKeys.get(task.getFileName())) {
                tempKeys.put(task.getFileName(), "1");
            } else {
                task = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }


    public static TreeTask getTreeTasks() {

        return MainFrom.getTreeTasks();
    }

    public static void setTreeTask(TreeTask task) {
        try {
            if (null == treeKeys.get(task.getRoot().getTitle())) {
                System.out.println("添加tree任务：" + task.getRoot().getTitle());
                MainFrom.treeTasks.add(task);
                treeKeys.put(task.getRoot().getTitle(), "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDownTask(DownTask task) {
        try {
            if (null == downKeys.get(task.getFileName())) {
                System.out.println("添加down任务：" + task.getFileName());
                MainFrom.downTasks.add(task);
                downKeys.put(task.getFileName(), "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}