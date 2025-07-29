package com.example.demo;

import auth.AuthService;
import auth.AuthServiceDefault;
import db.dao.UserDAO;
import db.dto.UserDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "auth", "db.dao"})
public class DemoApplication {
    public static void main(String[] args) {
//        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
//        AuthService authService = context.getBean("authServiceDefault", AuthService.class);
//        System.out.println(authService.login("barista.lee", "coffee123!"));
//        System.out.println(authService.login("barista.lee", "coffee123"));
    }
}
