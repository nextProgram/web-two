package com.example.webtwo.demo.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by Lihx on 2017/12/20.
 */
public class SFTPUtil {
    private static  final Logger logger = LoggerFactory.getLogger(SFTPUtil.class);

    /**
     * 链接sftp
     * @param host 主机
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public ChannelSftp connect(String host, int port, String username,
                               String password) {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            logger.info("Session创建成功");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            logger.info("Session已连接");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            logger.info("连接到主机" + host + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sftp;
    }

    /**
     * 文件上传
     *
     * @param directory 目录
     * @param uploadFile 要上传的文件,包括路径
     * @param sftp
     */
    public void upload(String directory, String uploadFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(uploadFile);
            sftp.put(new FileInputStream(file), file.getName());
            logger.info("上传文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载
     *
     * @param directory 目录
     * @param downloadFile 要下载文件名
     * @param saveFile 存在本地的路径
     * @param sftp
     */
    public void download(String directory, String downloadFile,
                         String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(saveFile);
            sftp.get(downloadFile, new FileOutputStream(file));
            logger.info("从银联获取勾兑结果文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件删除
     *
     * @param directory 目录
     * @param deleteFile 要删除的文件名
     * @param sftp
     */
    public void delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
            System.out.println("删除成功");
        } catch (Exception e) {
            System.out.println("删除失败");
            e.printStackTrace();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 目录
     * @param sftp
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory, ChannelSftp sftp)
            throws SftpException {
        return sftp.ls(directory);
    }

}
