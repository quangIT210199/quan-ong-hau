package com.codelovers.quanonghau.ProductUserTest;

import com.codelovers.quanonghau.models.Product;
import com.codelovers.quanonghau.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepoTest {

    @Autowired
    private ProductRepository productRepo;

    @Test
    public void testFindProductByAlias() {
        String alais = "Apple1";

        Product product = productRepo.findByAlias(alais);

        Assertions.assertThat(product).isNotNull();
    }
}
