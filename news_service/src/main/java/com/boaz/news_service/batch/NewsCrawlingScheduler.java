package com.boaz.news_service.batch;


import com.boaz.news_service.repository.CategoryRepository;
import com.boaz.news_service.repository.NewsRepository;
import com.boaz.news_service.service.CategoryService;
import com.boaz.news_service.vo.Category;
import com.boaz.news_service.vo.News;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@PropertySource("static/batch.properties")
public class NewsCrawlingScheduler {

    @Qualifier("webApplicationContext")
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    CacheManager cacheManager;

    static boolean flag = false;

    @Scheduled(fixedDelay = 3000000)
    @Async("asyncThreadTaskExecutor")
    public void crawling_naver_main_news() throws IOException, ParseException, java.text.ParseException {
        String news_list_url_format = "https://news.naver.com/main/list.nhn?" +
                "mode=LS2D" +
                "&mid=shm" +
                "&sid1=%s" +
                "&sid2=%s"+
                "&date=%s"+
                "&page=%d";
        Resource resource = resourceLoader.getResource("classpath:/static/newsClassification.json");

        Map<String, Map<String, String>> typeMap = getClassfication();
        for (String sid2 : typeMap.keySet()) {
            String sid1 = typeMap.get(sid2).get("sid1");
            String class1 = typeMap.get(sid2).get("class1");
            String class2 = typeMap.get(sid2).get("class2");

            int cnt = 0;
            if(sid1.equals("102")){ // 사회면만 크롤링
                Calendar calendar = Calendar.getInstance();
                String date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
                int page = 1;
                while (true) {
                    boolean hasNextDate = false;
                    while (true) {
                        String news_list_url = String.format(news_list_url_format, sid1, sid2, date, page);
                        Cache cache = cacheManager.getCache("newsListCache");
                        Document document = null;
                        if (ObjectUtils.isEmpty(cache.get(news_list_url))) {
                            document = Jsoup.connect(news_list_url).get();
                            String[] arr = new String[]{".type06_headline", ".type06"};
                            for (String str : arr) {
                                Elements newsList = document.select(str);
                                if (!ObjectUtils.isEmpty(newsList)) {
                                    newsList = newsList.select("li");
                                }
                                for (Element elem : newsList) {
                                    Elements aElems = elem.select("a");
                                    if (!ObjectUtils.isEmpty(aElems)) {
                                        String href = aElems.get(0).attr("href");
                                        cnt++;
                                        if(cnt<50)
                                            getNewsInfo(href, sid1, sid2, class1, class2);
                                    }
                                }
                            }
                        } else {
                            document = (Document) cache.get(news_list_url).get();
                            //log.info("news list doc from cache : {}",document.toString());
                        }
                        Elements pageElems = document.getElementsByClass("nclicks(fls.page)");
                        boolean hasNextPage = false;
                        if (!ObjectUtils.isEmpty(pageElems)) {
                            String lastPageStr = pageElems.last().text();
                            if (!"다음".equals(lastPageStr) && !"이전".equals(lastPageStr)) {
                                int lastPage = Integer.parseInt(pageElems.last().text());
                                if (lastPage > page) {
                                    hasNextPage = true;
                                }
                            } else if("다음".equals(lastPageStr)) {
                                hasNextPage = true;
                            }
                        }
                        if (!hasNextPage) {
                            page = 1;
                            Elements dateElems = document.getElementsByClass("nclicks(fls.date)");
                            if (!ObjectUtils.isEmpty(dateElems)) {
                                Element dateElem = dateElems.last();
                                String preDate = dateElem.attr("href");
                                Pattern pattern = Pattern.compile("[0-9]{8}");
                                Matcher matcher = pattern.matcher(preDate);
                                if (matcher.find()) {
                                    int curDate = Integer.parseInt(date);
                                    int date1 = Integer.parseInt(matcher.group());
                                    if (date1 < curDate && curDate>20210711) {
                                        hasNextDate = true;
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
                                        cal.add(Calendar.DAY_OF_MONTH,-1);
                                        date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
                                    }
                                }
                            }
                            break;
                        }
                        page++;
                    }
                    if (flag) {
                        String todayStr = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
                        int today = Integer.parseInt(todayStr);
                        int d = Integer.parseInt(date);
                        if (today - d >= 2) break;
                    }
                    if (!hasNextDate) {
                        date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
                        break;
                    }
                }
            }

        }
        flag = true;
    }

    private void getNewsInfo(String url,String sid1,String sid2, String class1, String class2) throws IOException, ParseException {

        if (hasNewsDoc(url)) return;
        if (newsRepository.existsByUri(url)) return;
        Pattern pattern1 = Pattern.compile("oid=[0-9]{3}");
        Matcher matcher = pattern1.matcher(url);
        String oid = "";
        if (matcher.find()) {
            String str = matcher.group();
            oid = str.substring(4);
        }

        Map<String, Map<String, String>> pressMap = getPressList();
        String mediaName = "";
        String rootDomain = "";
        if (pressMap.containsKey(oid)) {
            mediaName = pressMap.get(oid).get("mediaName");
            rootDomain = pressMap.get(oid).get("rootDomain");
        }

        Document document = Jsoup.connect(url).get();

        String title = "";
        String summary = "";
        String content = "";
        String pubDate = "";

        Elements titleElems = document.select("meta[property^=og:title]");
        Elements summaryElems = document.select(".media_end_summary");
        Elements contentElems = document.select("._article_body_contents");
        Elements dateElems = document.select(".t11");

        if (!ObjectUtils.isEmpty(titleElems)) {
            title = titleElems.get(0).attr("content");
        }
        if (!ObjectUtils.isEmpty(summaryElems)) {
            summary = summaryElems.get(0).text();
        }
        if (!ObjectUtils.isEmpty(contentElems)) {
            content = contentElems.get(0).text();
        }
        if (!ObjectUtils.isEmpty(dateElems)) {
            pubDate = dateElems.get(0).text();
        }

        if(summary.equals(""))
            return;
        if(content.equals(""))
            return;

        News news = new News();

        // get category id by name
        Long categoryId=0L;

        Map<Long, String> map;
        map = new HashMap<>();
        map.put(0L, "전체");
        map.put(1L, "교육");
        map.put(2L, "사건사고");
        map.put(3L, "노동");
        map.put(4L, "언론");
        map.put(5L, "환경");
        map.put(6L, "인권/복지");
        map.put(7L, "인물");
        map.put(8L, "사회일반");
        map.put(9L, "식품/의료");
        map.put(10L, "지역");

        for (Long key : map.keySet()) {
            if (class2.equals(map.get(key))) {
                categoryId = key;
            }
        }

        // save news
        news.setCategory(categoryId);
        news.setTitle(title);
        news.setContent(content);
        news.setLikes(1L);
        news.setViews(1L);
        news.setWriter(1L);
        news.setMedia(mediaName);
        news.setSummary(summary);
        news.setUri(url);

        newsRepository.save(news);

        return;

    }

    private Map<String, Map<String,String>> getPressList() {
        byte[] data;
        Map<String, Map<String,String>> map = new HashMap<>();
        try(InputStream in = getClass().getResourceAsStream("/static/pressList.json")) {
            data = IOUtils.toByteArray(in);

            String jsonStr = new String(data);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONObject jsonArray = (JSONObject) parser.parse(String.valueOf(jsonObject.get("pressList")));
            JSONArray jsonArray1 = (JSONArray) parser.parse(String.valueOf(jsonArray.get("press")));

            for (Object obj : jsonArray1) {
                Map<String, String> tempMap1 = (Map<String, String>)obj;
                String oid = tempMap1.get("oid");
                Map<String, String> tempMap2 = new HashMap<>();
                tempMap2.put("rootDomain",tempMap1.get("rootDomain"));
                tempMap2.put("mediaName",tempMap1.get("mediaName"));
                map.put(oid,tempMap2);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<String, Map<String, String>> getClassfication() {
        byte[] data;
        Map<String,Map<String, String>> map = new HashMap<>();
        try(InputStream in = getClass().getResourceAsStream("/static/newsClassification.json")) {
            data = IOUtils.toByteArray(in);

            String jsonStr = new String(data);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONObject jsonObject1 = (JSONObject) parser.parse(String.valueOf(jsonObject.get("classificationList")));
            JSONArray jsonArray = (JSONArray) parser.parse(String.valueOf(jsonObject1.get("classification")));

            for (Object obj : jsonArray) {
                Map<String, String> tempMap1 = (Map<String, String>) obj;
                Map<String, String> tempMap2 = new HashMap<>();

                tempMap2.put("class1",tempMap1.get("class1"));
                tempMap2.put("class2",tempMap1.get("class2"));
                tempMap2.put("sid1",tempMap1.get("sid1"));
                map.put(tempMap1.get("sid2"),tempMap2);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    private boolean hasNewsDoc(String url) {
        Cache cache = cacheManager.getCache("newsCache");
        if (ObjectUtils.isEmpty(cache.get(url))) {
            cache.put(url,url+"_value");
            return false;
        } else return true;
    }
}

