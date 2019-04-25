package com.example.webtwo.demo.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.List;

/**
 * Created by lhx
 */
public class FTPUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    /**
     * 初始化ftp
     * @param ftpIp IP地址
     * @param port 端口
     * @param usernme 用户名
     * @param pwd 密码
     * @return ftpClient
     */
    public FTPClient initFtpClient(String ftpIp,int port,String usernme,String pwd){
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        ftpClient = new FTPClient();
        try {
            logger.info("连接ftp服务器:"+ftpIp+":"+port);
            ftpClient.connect(ftpIp,port); //连接ftp服务器
            ftpClient.login(usernme,pwd); //登录ftp服务器
            logger.info("连接ftp成功");
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return ftpClient;
    }


   /* public static String readFtpFile(String fileName ){
        FTPClient ftp = new FTPClient();
        StringBuilder builder = null;
        try {
            ftp.connect(ftpIP,prot);
            ftp.login(ftpName, ftpPwd);
        }
        ftp.enterLocalPassiveMode();
        InputStream ins = ftp.retrieveFileStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
        String line;
        builder = new StringBuilder(150);
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        if (ins != null) {
            ins.close();
        }
// 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
        ftp.getReply();

    } catch (IOException e) {
        e.printStackTrace();
    }finally{
        try {
            ftp.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    	return builder.toString();

}*/

    /** * 下载文件 *
     * @param pathname FTP服务器文件目录 *
     * @param filename 文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return */
    public  boolean downloadFile(String pathname, String filename, String localpath,FTPClient ftpClient){
        boolean flag = false;
        OutputStream os=null;
        StringBuilder builder = null;
        try {
            logger.info("开始下载文件");
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(pathname);
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile file : ftpFiles){
                if(filename.equalsIgnoreCase(file.getName())){
                    File localFile = new File(localpath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
            logger.error("下载文件成功");
        } catch (Exception e) {
            logger.error("下载文件失败");
            e.printStackTrace();
        } finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }


    /**
     * 上传文件
     * @param remotePath 上传文件目录
     * @param fileList 上传文件
     * @param ftpClient
     * @return
     * @throws IOException
     */
    private boolean uploadFile(String remotePath, List<File> fileList, FTPClient ftpClient) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        try {
            logger.error("开始上传文件");
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            logger.info("开始处理文件");
            for (File fileItem : fileList) {
                fis = new FileInputStream(fileItem);
                ftpClient.storeFile(fileItem.getName(), fis);
            }
            logger.info("处理文件结束");
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            uploaded = false;
            e.printStackTrace();
        } finally {
            fis.close();
            ftpClient.disconnect();
        }
        return uploaded;
    }



}
