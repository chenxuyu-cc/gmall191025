package com.atguigu.gmall191025.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall191025.bean.UserAddress;
import com.atguigu.gmall191025.bean.UserInfo;
import com.atguigu.gmall191025.config.RedisUtil;
import com.atguigu.gmall191025.service.UserService;
import com.atguigu.gmall191025.user.mapper.UserAddressMapper;
import com.atguigu.gmall191025.user.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UserInfo> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public List<UserInfo> selectByName(UserInfo userInfo) {
        return null;
    }

    @Override
    public List<UserInfo> selectByLike(UserInfo userInfo) {
        return null;
    }

    @Override
    public void update(UserInfo userInfo) {

    }

    @Override
    public void add(UserInfo userInfo) {

    }

    @Override
    public void del(UserInfo userInfo) {

    }

    @Override
    public List<UserAddress> getUserAddressByUserId(String userId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    @Override
    public List<UserAddress> getUserAddressByUserId(UserAddress userAddress) {

        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        //获取密码
        String passwd = userInfo.getPasswd();
        //加密
        String newPasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        //查询用户
        userInfo.setPasswd(newPasswd);
        UserInfo user = userMapper.selectOne(userInfo);
        if(user!=null){
            //放到缓存
            Jedis jedis = null;

            try {
                jedis = redisUtil.getJedis();
                //创建key
                String userKey = userKey_prefix+user.getId()+userinfoKey_suffix;
                //设置过期时间
                jedis.setex(userKey,userKey_timeOut, JSON.toJSONString(user));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(jedis!=null){
                    jedis.close();
                }
            }
            return user;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {

        Jedis jedis = redisUtil.getJedis();

        String userKey = userKey_prefix+userId+userinfoKey_suffix;

        String userJson = jedis.get(userKey);

        if(!StringUtils.isEmpty(userJson)){
            UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
            return userInfo;
        }
        return null;
    }
}
