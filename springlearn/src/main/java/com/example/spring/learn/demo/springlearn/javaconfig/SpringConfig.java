package com.example.spring.learn.demo.springlearn.javaconfig;

import com.example.spring.learn.demo.springlearn.dao.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.example.spring.learn.demo.springlearn.javaconfig")
@PropertySource(value = "classpath:jdbc.properties",ignoreResourceNotFound = true)
public class SpringConfig {

    @Bean
    public UserDao getUserDao(){
        return new UserDao();
    }

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Bean(destroyMethod = "close")
    public DataSource dataSource(){
        
    }
}
