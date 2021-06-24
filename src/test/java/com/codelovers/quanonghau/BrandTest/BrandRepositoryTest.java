package com.codelovers.quanonghau.BrandTest;

import com.codelovers.quanonghau.models.Brand;
import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.repository.BrandRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // for test really database
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository repo;

    @Test
    public void testCreateBrand1(){
        Category laptops = new Category(5);
        Brand acer = new Brand("Dart");

        acer.getCategories().add(laptops);

        Brand savedBrand = repo.save(acer);

        Assertions.assertThat(savedBrand).isNotNull();
        Assertions.assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = repo.findAll();
        brands.forEach(System.out::println);

        Assertions.assertThat(brands).isNotEmpty();
    }

    @Test
    public void testGetId() {
        Brand brand = repo.findById(1).get();

        Assertions.assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    public void testUpdateName() {
        String name = "Dell i5";
        Brand acer = repo.findById(1).get();

        acer.setName(name);

        Brand savedBrand = repo.save(acer);
        Assertions.assertThat(savedBrand.getName()).isEqualTo(name);
    }

    @Test
    public void testDelete() {
        Integer id = 1;
        repo.deleteById(id);

        Optional<Brand> result = repo.findById(1);

        Assertions.assertThat(result).isEmpty();
    }
}
