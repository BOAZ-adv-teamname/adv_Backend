package com.boaz.news_service;

import com.boaz.news_service.repository.CategoryRepository;
import com.boaz.news_service.vo.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

/*
@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void create(){
        Category category = new Category();
        category.setCategoryName("정치");
        Category newCategory = categoryRepository.save(category);
        System.out.println(newCategory);
    }

    @Test
    public void read(){
        Optional<Category> category = categoryRepository.findById(1L);
        category.ifPresent(selectCategory ->{
            System.out.println("category: "+selectCategory);
        });
    }

    @Test
    @Transactional
    public void update(){
        Optional<Category> category = categoryRepository.findById(1L);
        category.ifPresent(selectCategory -> {
            selectCategory.setCategoryName("의료");
            Category newCategory = categoryRepository.save(selectCategory);
            System.out.println("category: "+newCategory);
        });
    }
}
*/