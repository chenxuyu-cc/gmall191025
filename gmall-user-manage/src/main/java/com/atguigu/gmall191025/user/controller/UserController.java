package com.atguigu.gmall191025.user.controller;

import com.atguigu.gmall191025.bean.UserInfo;
import com.atguigu.gmall191025.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("selectAll")
    public List<UserInfo> selectAll(){
        return userService.selectAll();
    }
}
