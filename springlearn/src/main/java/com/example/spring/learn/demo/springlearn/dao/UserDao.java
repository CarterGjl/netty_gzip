package com.example.spring.learn.demo.springlearn.dao;

import com.example.spring.learn.demo.springlearn.javaconfig.User;

import java.util.ArrayList;
import java.util.List;


public class UserDao {
    public List<User> queryUserList(){
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserName("user_name_"+i);
            user.setPwd("pwd_"+i);
            users.add(user);
        }
        return users;
    }
}
