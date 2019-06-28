package com.example.webtwo.demo.rabitMQ.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试消息的消费
 * @author lhx
 * @date 2019/6/21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MsgConsumeTest {

    @Autowired
    private MsgConsume msgConsume;

    @Test
    public void getMsg() {;

    }
}