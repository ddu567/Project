package dev.project.productservice.service;

import dev.project.productservice.entity.Product;
import dev.project.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private  ProductRepository productRepository;
    @Autowired
    private  RedisTemplate<String, String> redisTemplate;


    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        String cacheKey = "product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (product == null) {
            Optional<Product> dbProduct = productRepository.findById(id);
            if (dbProduct.isPresent()) {
                redisTemplate.opsForValue().set(cacheKey, dbProduct.get());
                return dbProduct;
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(product);
    }

    private void updateStockFromRedis(Product product) {
        String stock = redisTemplate.opsForValue().get("stock:" + product.getId());
        if (stock != null) {
            product.setStock(Integer.parseInt(stock));
        }
    }

    @Transactional
    public boolean purchaseProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getStock() > 0) {
                product.setStock(product.getStock() - 1);
                productRepository.save(product);
                redisTemplate.opsForValue().decrement("stock:" + id);
                return true;
            }
        }
        return false;
    }

    public Product save(Product product) {
        redisTemplate.opsForValue().set("stock:" + product.getId(), String.valueOf(product.getStock()));
        return productRepository.save(product);
    }
}
