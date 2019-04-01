package com.example.webtwo.demo.redis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lhx
 * @date 2018/12/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JedisSevviceTest {
    @Autowired
    private JedisSevvice jedisSevvice;

    @Test
    public void jedisTest() {
        jedisSevvice.jedisTest();
    }
    @Test
    public void jedisTest2() {
        jedisSevvice.jedisTest();
    }
}