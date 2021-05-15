package com.codelovers.quanonghau.CategoryUserTest;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryUserTest {

    @Autowired private CategoryRepository categoryRepo;

    @Test
    public void testCategoryEnable() {
        List<Category> categoryList = categoryRepo.findAllEnabled();

        for (Category c : categoryList) {
            System.out.println(c.getName() + " " + c.isEnabled());
        }
    }

    @Test
    public void testBreandCrumb() {
        Category category = categoryRepo.findById(7).get();
        System.out.println(category.getName());

        List<Category> listParents = getCategoryParentsQ(category);

        for (Category c : listParents) {
            System.out.println(c.getName() + " " + c.getId());
        }

    }

    public List<Category> getCategoryParentsQ(Category child) { // Using this for BreadCrumb, this while file all parent of Child
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();
        System.out.println(parent.getName() + " " + parent.getId());

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }

}
