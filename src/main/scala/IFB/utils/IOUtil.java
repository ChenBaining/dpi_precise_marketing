package IFB.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by miluo on 2017/9/7.
 * Created by miluo on 2017/9/7.
 */
public class IOUtil {

    public static String reader(String filename, String charset) {
        File file = new File(filename);
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            String tempString;
            while ((tempString = bufferedReader.readLine()) != null) {
                sb.append(tempString);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
