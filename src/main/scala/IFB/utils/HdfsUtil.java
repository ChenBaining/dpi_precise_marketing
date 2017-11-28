package IFB.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CainGao on 2017/8/30.
 */
public class HdfsUtil {
    /**
     * 删除目录下的文件
     *
     * @param file
     * @param historyDay
     */
    public static void deleteHistoryDir(String file, int historyDay) {
        Configuration conf = new Configuration();
        Path dstPath = new Path(file);
        try {
            long now = System.currentTimeMillis();
            FileSystem fs = dstPath.getFileSystem(conf);
            if (!fs.exists(dstPath)) return;
            FileStatus[] fileStatuses = fs.listStatus(dstPath);
            for (FileStatus fileStatus : fileStatuses) {
                if ((now - fileStatus.getModificationTime()) > (historyDay * 86400000)) {
                    deleteDir(fileStatus.getPath().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件夹下面的所有文件
     *
     * @param file
     * @return
     */
    public static List<String> list(String file) {
        List<String> dsts = new ArrayList<String>();
        Configuration conf = new Configuration();
        Path dstPath = new Path(file);
        try {
            FileSystem fs = dstPath.getFileSystem(conf);
            FileStatus[] fileStatuses = fs.listStatus(dstPath);
            for (FileStatus fileStatus : fileStatuses) {
                if (fileStatus.isDirectory()) {
                    dsts.add(fileStatus.getPath().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dsts;
    }

    /**
     * 删除文件列表
     *
     * @param paths
     */
    public static void deleteFiles(List<String> paths) {
        for (String path : paths) {
            deleteDir(path);
        }
    }

    /**
     * 给定文件名和文件内容，创建hdfs文件
     *
     * @param dst
     * @param contents
     * @throws IOException
     */
    public static void createFile(String dst, byte[] contents)
            throws IOException {
        Configuration conf = new Configuration();
        Path dstPath = new Path(dst);
        FileSystem fs = dstPath.getFileSystem(conf);
        FSDataOutputStream outputStream = fs.create(dstPath);
        outputStream.write(contents);
        outputStream.close();
        fs.close();
    }

    /**
     * 判断路径是否存在.
     */
    public static boolean exists(String hdfsPath) {
        Configuration conf = new Configuration();
        try {
            FileSystem fs = FileSystem.get(conf);
            Path path = new Path(hdfsPath);
            return fs.exists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通配符方式判断路径存不存在
     *
     * @param hdfsPath
     * @return
     */
    public static boolean pathExists(String hdfsPath) {
        Configuration conf = new Configuration();
        try {
            FileSystem fs = FileSystem.get(conf);
            FileStatus[] fss = fs.globStatus(new Path(hdfsPath));
            return fss.length > 0 ? true : false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除HDFS上的目录
     *
     * @param dir：目录路径
     */
    public static void deleteDir(String dir) {
        Configuration conf = new Configuration();
        try {
            FileSystem fs = FileSystem.get(conf);
            fs.delete(new Path(dir), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查存储目录，若存在，则删除
     *
     * @param path
     * @return
     */
    public static boolean checkSavePath(String path) {
        if (exists(path)) {
            deleteDir(path);
            return true;
        }
        return false;
    }

}
