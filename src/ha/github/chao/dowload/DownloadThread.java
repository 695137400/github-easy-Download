package ha.github.chao.dowload;

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
    public int ID;

    public boolean exit = false;

    public DownloadThread(int id) {
        ID = id;
    }

    @Override
    public void run() {
        Map<String,String> falg = new HashMap<>();
        while (!exit) {
            if (falg.size() == 0) {
                //从任务列表中读取一个没有被下载的任务
                DownTask downTask = singleton.getDownTasks();
                try {
                    if (null != downTask) {
                        falg.put(downTask.getFileName(),"1");
                        downTask.setRun(true);
                        downTask.downFile(falg);
                    }
                } catch (Exception e) {
                    downTask.setRun(false);
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
}