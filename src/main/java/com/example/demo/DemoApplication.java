package com.example.demo;

import db.dao.UserDAO;
import db.dto.UserDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "db.dao", "db.dto"})
public class DemoApplication {

    public static void main(String[] args) {
        //SpringApplication.run(DemoApplication.class, args);
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        // UserDAO 빈 가져오기
        UserDAO userDAO = context.getBean(UserDAO.class);

        // 테스트용 ID로 조회
        UserDTO user = userDAO.findById("barista.lee");

        if (user != null) {
            System.out.println("✅ User Found:");
            System.out.println("ID: " + user.getId());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Affiliation: " + user.getAffiliationCode());
        } else {
            System.out.println("User not found.");
        }

    }

}
