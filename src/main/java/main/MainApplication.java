package main;

import main.model.auth.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MainApplication.class, args);
        AuthService authService = context.getBean("authServiceDefault", AuthService.class);
        System.out.println(authService.login("barista.lee", "coffee123!"));
        System.out.println(authService.login("barista.lee", "coffee123"));
    }
}
