package com.atguigu.gmall191025.service;


import com.atguigu.gmall191025.bean.UserAddress;
import com.atguigu.gmall191025.bean.UserInfo;

import java.util.List;

public interface UserService {

    List<UserInfo> selectAll();

    //根据一个属性查询
    List<UserInfo> selectByName(UserInfo userInfo);


    //模糊like查询
    List<UserInfo> selectByLike(UserInfo userInfo);

    void update(UserInfo userInfo);

    void add(UserInfo userInfo);

    void del(UserInfo userInfo);

    List<UserAddress> getUserAddressByUserId(String userId);

    List<UserAddress> getUserAddressByUserId(UserAddress userAddress);
}

