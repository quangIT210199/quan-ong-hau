package com.codelovers.quanonghau.ProductRepositoryTest;

import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.models.Product;
import com.codelovers.quanonghau.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false) // DL test sẽ thêm vào db
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private TestEntityManager entityManager; // Sử dụng thao tác vs các models

    @Test // Add product
    public void testCreatedProduct() {
        //1 - N
        Category category = entityManager.find(Category.class, 1);

        Product product = new Product();

        product.setName("Quang3");
        product.setCategory(category);
        product.setAlias("Cake3");
        product.setFullDescription("Ngon");
        product.setShortDescription("Ngon");
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());
        product.setCost(200);
        product.setPrice(300);
        product.setDiscountPercent(20);
        product.setEnabled(true);
        product.setInStock(true);

        Product proSaved = productRepo.save(product);

        Assertions.assertThat(proSaved).isNotNull();
        Assertions.assertThat(proSaved.getId()).isGreaterThan(0);
    }

    @Test // In toan bo product
    public void testListAllProduct() {
        Iterable<Product> productIterable = productRepo.findAll();

        productIterable.forEach(System.out::println);
    }

    @Test // GET product by id
    public void testGetProduct() {
        Integer id = 1;
        Product product = productRepo.findById(id).get();

        System.out.println(product);

        Assertions.assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;

        Product p = productRepo.findById(id).get();
        p.setPrice(400);

        Product proSaved = productRepo.save(p);

        Assertions.assertThat(proSaved.getPrice()).isEqualTo(300);
    }

    @Test
    public void testDeleteProduct() {
        Integer id = 2;
        productRepo.deleteById(id);

        Optional<Product> p = productRepo.findById(id);

        Assertions.assertThat(!p.isPresent()); // ton tai ko
    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = productRepo.findById(productId).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra image 2.png");
        product.addExtraImage("extra image 3.png");

        Product proSaved = productRepo.save(product);

        Assertions.assertThat(proSaved.getImages().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer id = 11;
        Product product = productRepo.findById(id).get();

        product.addDetails("Nhân Nho", "80 calo");
        product.addDetails("Nhân Sầu Riêng", "60 calo");
        product.addDetails("Nhân Cam", "80 calo");

        Product savedPro = productRepo.save(product);

        Assertions.assertThat(savedPro.getDetails()).isNotEmpty();
    }
}
