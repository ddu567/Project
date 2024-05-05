package dev.project.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = "dev.project.productservice")
//@EnableJpaRepositories(basePackages = "dev.project.productservice.repository")
//@EntityScan("dev.project.productservice.entity")
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
