package dev.project;

import dev.project.entity.Product;
import dev.project.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            repository.save(new Product("Laptop", 999.99, 10, "High performance laptop"));
            repository.save(new Product("Smartphone", 699.99, 15, "Latest model smartphone"));
            repository.save(new Product("Tablet", 400.00, 20, "Tablet with high-resolution display"));
            repository.save(new Product("Smartwatch", 199.99, 30, "Smartwatch with health tracking features"));
            repository.save(new Product("Headphones", 59.99, 50, "Noise cancelling headphones"));
            repository.save(new Product("Portable Charger", 29.99, 70, "High capacity battery"));
            repository.save(new Product("Wireless Mouse", 24.99, 80, "Ergonomic wireless mouse"));
            repository.save(new Product("Webcam", 89.99, 40, "HD webcam for streaming"));
            repository.save(new Product("External Hard Drive", 120.00, 40, "1TB USB 3.0 external hard drive"));
            repository.save(new Product("Router", 129.99, 15, "High-speed internet router"));
        };
    }
}
