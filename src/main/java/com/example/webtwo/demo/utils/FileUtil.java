
package com.example.webtwo.demo.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
     * 在JDK的java.net包中已经提供了访问HTTP协议的基本功能的类：HttpURLConnection
     * HttpURLConnection是Java的标准类，它继承自URLConnection，可用于向指定网站发送GET请求、POST请求
     * @param urlPath 路径
     * @return
     */
    public static HttpURLConnection getConnectProperties(String urlPath) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(6000);
            conn.setRequestProperty("ContentType", "text/plain");
            conn.setRequestProperty("CHARSET", "UTF-8");
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

    /**
     * 从输入流中读取字节数组
     *
     * @param inputStream
     * @return
     */
    public static byte[] getBytesFromInputStream(InputStream inputStream) {
        //判断有效性
        if (inputStream == null) {
            return null;
        }
        try {
            //设置返回数组
            List<Integer> rs = new ArrayList<Integer>();
            int b = -1;
            while ((b = inputStream.read()) != -1) {
                rs.add(b);
            }
            byte[] rtn = new byte[rs.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = rs.get(i).byteValue();
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 按照日期获取文件
     * @param filePath 文件路径
     * @return
     */
    public static File[] getAppZipFile(String filePath) {
        File[] files = new File[]{};
        //根目录
        File root = new File(filePath);
        if (root.exists() && root.isDirectory() && root.canRead()) {
            List<File> list = new ArrayList<File>();
            File[] temp = root.listFiles();
            //遍历文件来获取符合条件的文件
            for (int i = 0; i < temp.length; i++) {
                //添加文件
                list.add(temp[i]);
            }
            files = new File[list.size()];
            list.toArray(files);
        }
        return files;
    }

    /**
     * 临时文件保存路径（路径下文件用完后删除）
     * @param tempRootPath 路径
     * @param fileSuffix 后缀
     * @return
     */
    public static String getTempPath(String tempRootPath,String fileSuffix){
        String tempFile = tempRootPath + File.separator + System.currentTimeMillis();
        return tempFile + fileSuffix;
    }

    /**
     * 获取二进制文件
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static byte[] readBinaryDataFromFile(String filePath) throws IOException{
        File file = new File(filePath);
        return FileUtils.readFileToByteArray(file);
    }

    /**
     * 删除文件
     * @param fileName 文件路径
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(String fileName) throws IOException{
        File file = new File(fileName);
        if(!file.exists()){
            return true;
        }
        if (file.isFile()){
            return file.delete();
        }else {
            throw new IOException("File["+ file +"]is not a normal file");
        }
    }
}
