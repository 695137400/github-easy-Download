package ha.github.chao.dowload;

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
public class DownloadThread extends Thread {

    final static Singleton singleton = Singleton.getInstance();

    //当前ID号
    private int ID;

    public DownloadThread(int id) {
        ID = id;
    }

    public boolean exit = true;

    @Override
    public void run() {
        Map<String, Object> falg = new HashMap<>();
        while (exit) {
            System.out.println("DownloadThread：" + ID);
            if (falg.size() < 2) {
                falg.put("ID", ID);
                DownTask downTask = singleton.getDownTasks();
                try {
                    if (null != downTask) {
                        LOG.debug("线程：\t" + ID + "\t任务：\t" + downTask.getFileName(), "Singleton.log");
                        falg.put(downTask.getFileName(), "1");
                        downTask.downFile(falg);
                    }
                } catch (Exception e) {
                    downTask.setRun(false);
                    falg.clear();
                    LOG.debug("线程：\t" + ID + "\t任务失败：\t" + downTask.getFileName() + "\t" + e.getMessage(), "Singleton.log");
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