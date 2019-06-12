package com.example.webtwo.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @author lhx
 * @date 2019/4/25
 */
public class StreamUtil {
    private final static Logger logger = LoggerFactory.getLogger(StreamUtil.class);
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * 使用PrintWriter发送字符流，已获取日志目录为例进行说明
     * @param dirUrl  日志完整路径
     * @param request HttpServletRequest
     * @return
     */
    public String logDir(String dirUrl, HttpServletRequest request) {
        logger.info("日志路径：" + dirUrl);
        String requestResult = "";
        PrintWriter requestOut = null;
        BufferedReader requestIn = null;
        try {
            HttpURLConnection conn = FileUtil.getConnectProperties(dirUrl);
            //发送请求
            request.setCharacterEncoding("UTF-8");
            requestOut = new PrintWriter(conn.getOutputStream());
            requestOut.println("");//设置post请求参数
            requestOut.flush();
            //获取请求结果代码
            int code = conn.getResponseCode();
            //返回成功状态码
            if (code == 200) {
                requestIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //获取结果
                while ((requestResult = requestIn.readLine()) != null) {
                    logger.info("获取结果：" + requestResult);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                requestIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                requestOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return requestResult;
    }

    /**
     * 压缩并打包文件
     * 将压缩包写入response输出流
     * @param filePath 文件完整路径
     */
    public void filePackage(String filePath, HttpServletResponse response) {
        logger.info("文件路径:[{}]", filePath);
        byte[] appFileByte = null;
        try {
            //获取文件
            File[] appFile = FileUtil.getAppZipFile(filePath);
            if (appFile != null && appFile.length > 0) {
                //设置临时路径
                String tempFile = FileUtil.getTempPath(filePath, "_temp.zip");
                //压缩文件
                FileUtil.zipFiles(tempFile, appFile);
                //读取临时文件
                appFileByte = FileUtil.readBinaryDataFromFile(tempFile);
                //删除临时文件
                FileUtil.deleteFile(tempFile);
            } else {
                logger.error("不存在文件");
            }
            //返回数据
            response.getOutputStream().write(appFileByte);
            response.flushBuffer();
            response.getOutputStream().close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 输出到浏览器下载
     * @param fileName 文件名称
     * @param fileStream 文件字节数组
     * @param response httpServletResponse
     */
    public void explorerDownload(String fileName, byte[] fileStream, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        if (fileStream == null || fileStream.length < 0) {
            return;
        }
        //将字节转为字节流，便于输出到浏览器下载
        in = new ByteArrayInputStream(fileStream);
        //设置下载名字
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName + ".zip");
        //设置下载类型
        response.setContentType("application/octet-stream");
        try {
            //输出流
            out = new BufferedOutputStream(response.getOutputStream());
            //写入输出流
            byte[] buff = new byte[4096];
            int i = 0;
            while ((i = in.read(buff)) > 0){
                out.write(buff,0,i);
            }
            logger.info("下载成功，文件名["+fileName+".zip]");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally {
            try {
                if(in != null){
                    in.close();
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
            try {
                if(out != null){
                    out.close();
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }
}
