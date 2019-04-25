
package com.example.webtwo.demo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author lhx
 * @date 2019/4/19
 */
public class FileUtil {
    private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取连接属性
     * @param urlPath 路径
     * @return
     */
    public static HttpURLConnection getConnectProperties(String urlPath){
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(6000);
            conn.setRequestProperty("ContentType","text/plain");
            conn.setRequestProperty("CHARSET","UTF-8");
            return conn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将所有文件压缩到输出流
     *
     * @param fileName 文件名
     * @param fs       文件列表
     */
    public static void zipFiles(String fileName, File[] fs) {
        String[] tmp = new String[fs.length];
        for (int i = 0; i < tmp.length; i++) {
            //获取绝对路径
            tmp[i] = fs[i].getAbsolutePath();
        }
        zipFiles(fileName, tmp);
    }

    /**
     * 将所有文件压缩到输出流
     *
     * @param fileName          zip文件
     * @param fileAbsolutePaths 文件绝对路径列表
     */
    public static void zipFiles(String fileName, String[] fileAbsolutePaths) {
        int len = Integer.MAX_VALUE;
        int ind = 0;
        for (int i = 0; i < fileAbsolutePaths.length; i++) {
            if (i == 0) {
                len = fileAbsolutePaths[i].length();
            }
            if (fileAbsolutePaths[i].length() < len) {
                ind = i;
            }
        }
        File tempFile = new File(fileAbsolutePaths[ind]).getParentFile();//获取父目录
        String tempStr = "";
        while (tempFile != null) {
            tempStr = tempFile.getAbsolutePath() + File.separator;
            boolean flag = true;
            for (int i = 0; i < fileAbsolutePaths.length && flag; i++) {
                if (fileAbsolutePaths[i].indexOf(tempStr) != 0) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                tempFile = null;
            } else {
                tempFile = tempFile.getParentFile();
            }
        }
        File[] tmpFs = new File[fileAbsolutePaths.length];
        for (int j = 0; j < tmpFs.length; j++) {
            tmpFs[j] = new File(fileAbsolutePaths[j]);
        }
        if (tempStr == null) {
            tempStr = "/";
        }
        zipFiles(fileName, tmpFs, tempStr);
    }

    /**
     * 压缩指定的文件集合
     *
     * @param fileName
     * @param fs
     * @param base
     */
    public static void zipFiles(String fileName, File[] fs, String base) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileName));
            base = new File(base).getAbsolutePath();
            for (int i = 0; i < fs.length; i++) {
                String baseDir = "";
                //判断目录存在
                if (fs[i].isDirectory()) {
                    File[] files = fs[i].listFiles();
                    baseDir = fs[i].getAbsolutePath().substring(base.length() + File.separator.length());
                    base = base + File.separator + baseDir + File.separator;
                    zipFiles(fileName, files, base);
                }
                baseDir = fs[i].getAbsolutePath().substring(base.length() + File.separator.length());
                out.putNextEntry(new ZipEntry(baseDir));
                FileInputStream in = new FileInputStream(fs[i]);
                int len = -1;
                byte[] ctx = new byte[1024 * 512];
                while ((len = in.read(ctx)) > 0) {
                    out.write(ctx, 0, len);
                }
                in.close();
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常，压缩文件失败", e);
        }
    }
}
