package com.atguigu.gmall191025.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private JedisPool jedisPool;

    public void initJedisPool(String host,int port,int timeOut,int database){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        //连接池最大连接参数
        jedisPoolConfig.setMaxTotal(200);

        //设置等待时间
        jedisPoolConfig.setMaxWaitMillis(10*1000);

        //最少剩余数
        jedisPoolConfig.setMinIdle(10);

        //排队等待
        jedisPoolConfig.setBlockWhenExhausted(true);

        // 设置当用户获取到jedis 时，做自检看当前获取到的jedis 是否可以使用
        jedisPoolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(jedisPoolConfig,host,port,timeOut);

    }

    //获取jedis
    public Jedis getJedis(){

        Jedis jedis = jedisPool.getResource();

        return jedis;
    }
}
