package com.atguigu.gmall191025.order.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall191025.bean.UserAddress;
import com.atguigu.gmall191025.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    @Reference
    private UserService userService;

    @RequestMapping("trade")
    public List<UserAddress> getUserAddressByUserId(String userId){
        return userService.getUserAddressByUserId(userId);

    }

    @RequestMapping("trade1")
    public List<UserAddress> getUserAddressByUserId(UserAddress userAddress){
        return userService.getUserAddressByUserId(userAddress);

    }


}
