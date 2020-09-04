package com.ggs.elasticsearch;

import com.ggs.elasticsearch.entity.Emp;
import com.ggs.elasticsearch.repository.EmpElasticsearchDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

/**
 * @Author Starbug
 * @Date 2020/9/3 17:07
 */
@SpringBootTest
public class TestElasticsearchRepository {

    @Autowired
    private EmpElasticsearchDao empElasticsearchDao;


    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    //保存或更新一条文档
    //id存在则更新,不存在则添加
    @Test
    public void testAdd() {
        DateTime dateTime = new DateTime(new Date());

        Emp emp = new Emp();
        emp.setContent("调查团的全新故事，有人目击到成群的风漂龙从古代树森林飞走。意识到此事并不寻常的调查团决定进行追踪调查，乘上第三期团的热气球，远渡大海。出现在波涛之后的是被冰雪封锁的世界，以及栖息于此的生态系统。");
        emp.setName("怪物猎人").setBir(new Date()).setId("6d1a3047-0ede-4bf0-a543-a32ca407299f").setAge(20).setAddress("我的世界");
        empElasticsearchDao.save(emp);

    }

    //根据id删除
    @Test
    public void testDeleteById() {
        empElasticsearchDao.deleteById("1");
    }

    //删除所有
    @Test
    public void testDeleteAll() {
        empElasticsearchDao.deleteAll();
    }

    //检索一条记录
    @Test
    public void testFindOne() {
        Optional<Emp> empOptional = empElasticsearchDao.findById("6d1a3047-0ede-4bf0-a543-a32ca407299f");
        Emp emp = empOptional.get();
        System.out.println(emp);
    }

    //降序查询所有
    @Test
    public void testFindAll() {
        Iterable<Emp> all = this.empElasticsearchDao.findAll(Sort.by(Sort.Order.desc("age")));
        all.forEach(emp -> System.out.println(emp));
    }

    //分页查询
    @Test
    public void testFindPage() {
        //PageRequest.of 参数1:当前页(从0算起)  参数2:查询多少条
        Page<Emp> search = this.empElasticsearchDao.search(QueryBuilders.matchAllQuery(), PageRequest.of(1, 1));

        List<Emp> content = search.getContent();
        for (Emp emp : content) {
            System.out.println(emp);
        }
    }

    //基础查询
    @Test
    public void testQuery() {
        List<Emp> empList1 = this.empElasticsearchDao.findByName("怪物猎人");
        List<Emp> empList2 = this.empElasticsearchDao.findByNameAndAge("怪物猎人", 20);
        List<Emp> empList3 = this.empElasticsearchDao.findByAgeGreaterThan(19);
        empList3.forEach(emp -> System.out.println(emp));
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //复杂查询
    @Test
    public void testQueryComplicate() throws IOException {
        SearchRequest searchRequest = new SearchRequest("company");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").postTags("</span>").preTags("<span style='color:red'").requireFieldMatch(false);
        sourceBuilder.query(QueryBuilders.termQuery("content","故事"))
                .sort("age", SortOrder.DESC)
                .from(0)
                .size(20)
                .highlighter(highlightBuilder);

        searchRequest.types("emp").source(sourceBuilder);

        SearchResponse search = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        for (SearchHit hit : hits) {
            Emp emp = gson.fromJson(hit.getSourceAsString(), Emp.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            String content = highlightFields.get("content").fragments()[0].toString();
            emp.setContent(content);
            System.out.println(emp);
        }
    }


}
