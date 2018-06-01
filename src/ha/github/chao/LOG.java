package ha.github.chao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/11/25.
 */
public class LOG {
    static String path = "./";

    public static void debug(String info, String fileName) {
        try {
            if (!new File(path).exists()) {
                new File(path).mkdirs();
            }
            File file = new File(path + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer w = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
            Date date = new Date();
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            w.write(time.format(date));
            w.write("\t");
            w.write(info);
            w.write("\n");
            w.flush();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
