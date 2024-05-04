import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "dev.project")
@ComponentScan(basePackages = {"dev.project.orderservice", "dev.project.userservice"})
@EnableFeignClients
@EnableJpaRepositories(basePackages = "dev.project.orderservice.repository")
@EntityScan(basePackages = {"dev.project.orderservice.entity", "dev.project.productservice.entity", "dev.project.userservice.entity"})
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
