package com.example.webone.demo.redis;

import com.example.webone.demo.base.ReturnVO;
import com.example.webone.demo.base.service.FallBackOfJedis;
import com.example.webone.demo.redis.vo.JedisRequestVO;
import com.example.webone.demo.redis.vo.JedisReturnVO;
import com.netflix.client.http.HttpRequest;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author lhx
 * @date 2018/12/12
 */
@FeignClient(name = "redis",fallback = FallBackOfJedis.class)
public interface RedisUseInterface {

    @PostMapping(value = "/jedisStr/jedisForStringSet")
    ReturnVO<JedisReturnVO> jedisForStringSet(@RequestBody JedisRequestVO jedisRequestVO);

    @PostMapping(value = "/jedisStr/jedisForStringSetTest",headers = {"content-type=application/json"})
    public ReturnVO<JedisReturnVO> jedisForStringSetTest(@RequestBody JedisRequestVO jedisRequestVO);
}
