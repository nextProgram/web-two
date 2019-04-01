package com.example.webtwo.demo.redis;

import com.example.webtwo.demo.base.ReturnVO;
import com.example.webtwo.demo.base.service.FallBackOfJedis;
import com.example.webtwo.demo.redis.vo.JedisRequestVO;
import com.example.webtwo.demo.redis.vo.JedisReturnVO;
import org.springframework.cloud.openfeign.FeignClient;
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
