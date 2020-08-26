package com.ggs.elasticsearch;

import com.ggs.elasticsearch.entity.Emp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @Author Starbug
 * @Date 2020/8/26 16:47
 */
public class TestDocumentFilterQuery {

    private TransportClient transportClient;

    @Before
    public void init() throws UnknownHostException {
        this.transportClient = new PreBuiltTransportClient(Settings.EMPTY);
        this.transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.232.130"), 9300));
    }

    @After
    public void destroy() {
        this.transportClient.close();
    }

    /**
     * 过滤查询: 主要用在查询执行之前对大量数据进行筛选
     * postFilter 用来设置过滤条件
     */
    @Test
    public void testQuery() {
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").gte(20).lte(55);

        SearchResponse searchResponse = this.transportClient.prepareSearch("ems")
                .setTypes("emp")
                .setPostFilter(rangeQueryBuilder)
                .setQuery(QueryBuilders.matchAllQuery())
                .get();

        System.out.println("最大得分 = " + searchResponse.getHits().getMaxScore());
        System.out.println("总条数 = " + searchResponse.getHits().getTotalHits());

        SearchHit[] hits = searchResponse.getHits().getHits();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        for (SearchHit hit : hits) {
            String jsonStr = hit.getSourceAsString();
            Emp emp = gson.fromJson(jsonStr, Emp.class);
            System.out.println(emp);
        }
    }

    /**
     * 基于多字段关键词查询
     * 分页
     * 排序
     * 过滤
     * 指定字段返回
     * 高亮处理
     */
    @Test
    public void multiConditionQuery() {

        //指定字段返回,除id字段,返回其他所有字段
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource().fetchSource("*", "id");
        //设置高亮的配置
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("*")
                .preTags("<span style='color:red'>")
                .postTags("</span>")
                .requireFieldMatch(false);//true: 除指定搜索字段外的其他字段有匹配的词,不会显示高亮 false: 除指定搜索字段外的其他字段有匹配的词,也会显示高亮
        //设置查询条件
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("喜欢框架课程和引擎课程", "name", "content");
        //设置过滤条件
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").gte(0).lte(30);

        //重点,这里拼接条件有顺序,否则会被条件无效
        SearchResponse searchResponse = this.transportClient.prepareSearch("ems")
                .setTypes("emp")
                .setSource(searchSourceBuilder)
                .setPostFilter(rangeQueryBuilder)
                .setQuery(multiMatchQueryBuilder)
                .addSort("age", SortOrder.ASC) //升序
                .highlighter(highlightBuilder)
                .setFrom(0)
                .setSize(3)
                .get();

        System.out.println("最大得分 = " + searchResponse.getHits().getMaxScore());
        System.out.println("总条数 = " + searchResponse.getHits().getTotalHits());

        SearchHit[] hits = searchResponse.getHits().getHits();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        for (SearchHit hit : hits) {
            Emp emp = gson.fromJson(hit.getSourceAsString(), Emp.class);
//            System.out.println(emp);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("name")) {
                emp.setName(highlightFields.get("name").fragments()[0].toString());
            }
            if (highlightFields.containsKey("content")) {
                emp.setContent(highlightFields.get("content").fragments()[0].toString());
            }
            System.out.println(emp);
//            System.out.println(hit.getSourceAsString());
//            System.out.println(hit.getHighlightFields());
        }
    }


    @Test
    public void testSearch() {
        SearchResponse searchResponse = this.transportClient.prepareSearch("ems")
                .setTypes("emp")
                .setFrom(0)
                .setSize(20)
                .addSort("age", SortOrder.DESC)
                .setSource(SearchSourceBuilder.searchSource().fetchSource("*", "bir"))
                .setPostFilter(QueryBuilders.rangeQuery("age").gte(0).lte(100))
                .setQuery(QueryBuilders.multiMatchQuery("框架爱好者", "name", "content"))
                .highlighter(new HighlightBuilder().field("*").requireFieldMatch(false).preTags("<span style='color:red'>").postTags("</span>"))
                .get();


        System.out.println("最大得分 = " + searchResponse.getHits().getMaxScore());
        System.out.println("总条数 = " + searchResponse.getHits().getTotalHits());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            System.out.println(hit.getHighlightFields());
        }

    }

}
