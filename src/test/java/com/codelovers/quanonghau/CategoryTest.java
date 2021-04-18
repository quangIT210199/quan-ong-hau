package com.codelovers.quanonghau;


import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false) // DL test sẽ thêm vào db
public class CategoryTest {

    @Autowired
    CategoryRepository categoryRepo;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Laptop");
        Category saveCate = categoryRepo.save(category);

        // chajy khi id > 0
        Assertions.assertThat(saveCate.getId()).isGreaterThan(0);
    }

    // Tao sub cate
    @Test
    public void testCreateSubCategory() {
        Category parent = new Category(5);
        Category memory = new Category("memory", parent);

//        Category cook = new Category("cook", parent);
        List<Category> cate = new ArrayList<>();
//        cate.add(camera);
        cate.add(memory);
        categoryRepo.saveAll(cate);
//        Category savedCategory =categoryRepo.save(subCategory);


//        Assertions.assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    //Hien thi
    @Test
    public void testGetCategory() {
        Category category = categoryRepo.findById(2).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();

        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }

        Assertions.assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = categoryRepo.findAll();

        for (Category category : categories) {

            if(category.getParent() == null) { // root
                System.out.println(category.getName());
                // get child
                Set<Category> child = category.getChildren();
                for (Category c : child) {
                    System.out.println("--" + c.getName());

                    printChildren(c, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());
            printChildren(subCategory, newSubLevel);
        }
    }

    @Test
    public void testListRootCategories() {
        List<Category> rootCategories = categoryRepo.findRootCategories();
        rootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName() {
        Category category = categoryRepo.findByName("Computer2");

        Assertions.assertThat(category.getName()).isEqualTo("Computer2");
        Assertions.assertThat(category).isNotNull();

    }

    @Test
    public void testFindByAlias() {
        Category category = categoryRepo.findByAlias("computer2");

        Assertions.assertThat(category).isNotNull();
        Assertions.assertThat(category.getAlias()).isEqualTo("computer2");
    }
}
