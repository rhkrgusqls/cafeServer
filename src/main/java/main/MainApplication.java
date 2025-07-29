package main;

import main.model.auth.AuthService;
import main.model.db.dao.ItemStockDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MainApplication.class, args);

        try {
            ItemStockDAO itemStockDAO = context.getBean(ItemStockDAO.class);
            itemStockDAO.decreaseStock(1, 101, 5); // 재고 감소 실행
        } catch (Exception e) {
            System.out.println("재고 부족: " + e.getMessage());
        }
    }
}