package com.boaz.news_service;

import com.boaz.news_service.repository.NewsListRepository;
import com.boaz.news_service.vo.NewsList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/*
@SpringBootTest
public class NewsListRepositoryTest {

    @Autowired
    private NewsListRepository newsListRepository;

    @Test
    public void readAll(){
        List<NewsList> news = newsListRepository.findAll();
        for(NewsList n : news) {
            System.out.println(n);
        }
    }
}

*/