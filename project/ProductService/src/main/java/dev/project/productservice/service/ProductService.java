package dev.project.productservice.service;

import dev.project.productservice.entity.Product;
import dev.project.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void createDummyProducts() {
        Product reservedProduct = new Product();
        reservedProduct.setTitle("예약구매 상품");
        reservedProduct.setDescription("특정한 시간에 열리는 예약구매 상품");
        reservedProduct.setPrice(100.0);
        reservedProduct.setStock(10);
        reservedProduct.setReserved(true);
        // 특정 시간 설정
        reservedProduct.setAvailableFrom(LocalDateTime.now().plusDays(2));
        reservedProduct.setAvailableUntil(LocalDateTime.now().plusDays(5));
        productRepository.save(reservedProduct);

        Product normalProduct = new Product();
        normalProduct.setTitle("일반 상품");
        normalProduct.setDescription("일반 상품");
        normalProduct.setPrice(50.0);
        normalProduct.setStock(20);
        normalProduct.setReserved(false);
        productRepository.save(normalProduct);
    }

    // 모든 상품 목록 조회
    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    // 상품 ID로 상품 조회
    public Optional<Product> getProductById(Long id) {
        // Redis 캐시에서 먼저 조회하고, 없으면 DB에서 조회하여 캐시에 저장 후 반환
        String cacheKey = "product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (product == null) {
            Optional<Product> dbProduct = productRepository.findById(id);
            if (dbProduct.isPresent()) {
                redisTemplate.opsForValue().set(cacheKey, dbProduct.get());
                return dbProduct;
            }
            return Optional.empty();
        }
        return Optional.of(product);
    }

    // 상품 정보 저장
    public Product save(Product product) {
        redisTemplate.opsForValue().set("stock:" + product.getId(), String.valueOf(product.getStock()));
        return productRepository.save(product);
    }

    // 상품 구매 처리
    public boolean purchaseProduct(Long id) {
        // 상품 재고 확인 후 구매 처리
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


    // 상품 재고 감소 (트랜잭션 처리)
    @Transactional
    public void reduceProductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Product not found with ID: " + productId));
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock for product ID: " + productId);
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    // 상품 정보 업데이트
    public Product updateProduct(Product product) {
        Long productId = product.getId();
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setTitle(product.getTitle());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setStock(product.getStock());
            updatedProduct.setReserved(product.isReserved());
            updatedProduct.setAvailableFrom(product.getAvailableFrom());
            updatedProduct.setAvailableUntil(product.getAvailableUntil());
            productRepository.save(updatedProduct);

            // 캐시 업데이트
            String cacheKey = "product:" + productId;
            redisTemplate.opsForValue().set(cacheKey, updatedProduct);
            return updatedProduct;
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    // Redis에 재고 수량 정보 저장
    public void saveStockToRedis(Long productId, int stock) {
        redisTemplate.opsForValue().set("stock:" + productId, stock);
    }
}
