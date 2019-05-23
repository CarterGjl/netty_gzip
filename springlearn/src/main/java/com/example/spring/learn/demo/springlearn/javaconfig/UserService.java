package com.example.spring.learn.demo.springlearn.javaconfig;

import com.example.spring.learn.demo.springlearn.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<User> queryUserList(){
        return userDao.queryUserList();
    }
}
