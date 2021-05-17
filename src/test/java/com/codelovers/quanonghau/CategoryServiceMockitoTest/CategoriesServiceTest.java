package com.codelovers.quanonghau.CategoryServiceMockitoTest;

import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.repository.CategoryRepository;
import com.codelovers.quanonghau.service.impl.CategoryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class) // Create fake db for testing
@ExtendWith(SpringExtension.class)
public class CategoriesServiceTest {

    @MockBean
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryServiceImpl categorySer;

    @Test
    public void testCheckUniqueInNewModelReturnDuplicateName() {
        Integer id = null;
        String name = "Computer";
        String alais = "abc";

        Category category = new Category(id, name, alais);

        Mockito.when(categoryRepo.findByName(name)).thenReturn(category);

        String rs = categorySer.checkUnique(id, name, alais);

        Assertions.assertThat(rs).isEqualTo("Duplicate Name @@");
    }

    @Test
    public void testCheckUniqueInNewModelReturnDuplicateAlais() {
        Integer id = null;
        String name = "hehe";
        String alais = "computer";
        Category category = new Category(id, name, alais);

        Mockito.when(categoryRepo.findByAlias(alais)).thenReturn(category);

        String rs = categorySer.checkUnique(id, name, alais);

        Assertions.assertThat(rs).isEqualTo("Duplicate Alias @@");
    }
}
