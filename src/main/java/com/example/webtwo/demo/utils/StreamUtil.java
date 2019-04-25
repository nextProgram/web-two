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
     * 使用PrintWriter发送字符流
     * @param dirUrl
     * @param request
     * @param response
     * @return
     */
    public String logDir(String dirUrl, HttpServletRequest request, HttpServletResponse response) {
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
        }finally {
            try {
                requestIn.close();
                requestOut.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return requestResult;
    }
}
