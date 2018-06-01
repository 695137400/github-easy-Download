package ha.github.chao;

import ha.github.chao.dowload.DownTask;
import ha.github.chao.dowload.TreeTask;

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
    private static String lock = "1";

    //静态工厂方法
    public static Singleton getInstance() {
        return single;
    }

    public static DownTask getDownTasks() {
        DownTask task = null;
        synchronized (lock) {
            try {
                task = MainFrom.getDownTasks();
                if (null != task) {
                    task.setRun(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return task;
    }


    public static TreeTask getTreeTasks() {
        TreeTask task = null;
        synchronized (lock) {
            try {
                task = MainFrom.getTreeTasks();
                if (null != task) {
                    task.setRun(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return task;
    }

    public static void setTreeTask(TreeTask task) {
        try {
            synchronized (lock) {
                LOG.debug("setTreeTask:\t" + task.getRoot().getTitle(), "Singleton.log");
                MainFrom.treeTasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDownTask(DownTask task) {
        try {
            synchronized (lock) {
                LOG.debug("setDownTask:\t" + task.getFileName(), "Singleton.log");
                MainFrom.downTasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}