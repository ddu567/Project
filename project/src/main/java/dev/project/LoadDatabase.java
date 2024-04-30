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
            repository.save(new Product("Laptop", "High performance laptop with SSD storage and dedicated graphics card", 999.99, 10));
            repository.save(new Product("Smartphone", "Latest model smartphone with high-resolution display and multiple cameras", 699.99, 15));
            repository.save(new Product("Tablet", "Tablet with high-resolution display and long battery life", 400.00, 20));
            repository.save(new Product("Smartwatch", "Smartwatch with health tracking features including heart rate monitor and step counter", 199.99, 30));
            repository.save(new Product("Headphones", "Noise cancelling headphones with comfortable ear cushions and long battery life", 59.99, 50));
            repository.save(new Product("Portable Charger", "High capacity portable charger with fast charging capability", 29.99, 70));
            repository.save(new Product("Wireless Mouse", "Ergonomic wireless mouse with customizable buttons and precise tracking", 24.99, 80));
            repository.save(new Product("Webcam", "HD webcam with built-in microphone and auto-focus feature", 89.99, 40));
            repository.save(new Product("External Hard Drive", "1TB USB 3.0 external hard drive for storing large files and backups", 120.00, 40));
            repository.save(new Product("Router", "High-speed internet router with dual-band Wi-Fi and multiple Ethernet ports", 129.99, 15));
            repository.save(new Product("Keyboard", "Mechanical gaming keyboard with RGB lighting", 79.99, 25));
            repository.save(new Product("Monitor", "27-inch gaming monitor with high refresh rate", 299.99, 10));
            repository.save(new Product("Printer", "All-in-one printer with wireless printing capability", 149.99, 20));
            repository.save(new Product("Gaming Chair", "Ergonomic gaming chair with lumbar support", 199.99, 15));
            repository.save(new Product("VR Headset", "Virtual reality headset with motion tracking", 399.99, 5));
        };
    }
}
