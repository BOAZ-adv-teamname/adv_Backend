package com.boaz.news_service;

import com.boaz.news_service.repository.NewsListRepository;
import com.boaz.news_service.repository.NewsRepository;
import com.boaz.news_service.vo.News;
import com.boaz.news_service.vo.NewsList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

/*
@SpringBootTest
public class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsListRepository newsListRepository;

    @Test
    public void create(){
        News news = new News();

        news.setCategory(1L);
        news.setTitle("제목");
        news.setContent("내용무");
        news.setWriter(1L);

        News newsBoard = newsRepository.save(news);
        System.out.println(newsBoard);

    }

    @Test
    public void read(){
        Optional<News> news = newsRepository.findById(1L);
        news.ifPresent(selectNews -> {
            System.out.println("board:"+selectNews);
        });
    }

    @Test
    public void readAll(){
        List<News> news = newsRepository.findAll();
        for(News board : news) {
            System.out.println(board);
        }
    }
    @Test
    public void readPage() {
        String category = "전체";
        int page = 0;
        int size = 3;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<NewsList> news = newsListRepository.findAllByCategory(category, pageRequest).getContent();
        news.forEach(board -> System.out.println(board.toString()));
    }
}
*/
