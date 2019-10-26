package com.atguigu.gmall191025.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall191025.bean.UserAddress;
import com.atguigu.gmall191025.bean.UserInfo;
import com.atguigu.gmall191025.service.UserService;
import com.atguigu.gmall191025.user.mapper.UserAddressMapper;
import com.atguigu.gmall191025.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

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
        userAddress.getUserId();
        return userAddressMapper.select(userAddress);
    }

    @Override
    public List<UserAddress> getUserAddressByUserId(UserAddress userAddress) {
        return userAddressMapper.select(userAddress);
    }
}
