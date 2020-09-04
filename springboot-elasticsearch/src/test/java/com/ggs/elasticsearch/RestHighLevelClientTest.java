package com.ggs.elasticsearch;

import com.ggs.elasticsearch.entity.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class RestHighLevelClientTest {

    //用于复杂查询
    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    //文档相关操作
    //删除
    @Test
    public void testDeleteDoc() throws IOException {
        //参数1:索引  参数2:类型  参数3:删除id
        DeleteRequest deleteRequest = new DeleteRequest("star", "book", "1");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    //添加
    @Test
    public void testAddDoc() throws IOException {
        IndexRequest indexRequest = new IndexRequest("star", "book");
        Book book = new Book();
        book.setName("小米").setAddress("中国").setAge(100).setBir(new Date()).setContent("红米K30Pro");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        indexRequest.source(gson.toJson(book), XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    //查询1
    @Test
    public void testSearchDoc() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        SearchRequest searchRequest = new SearchRequest("star");
        searchRequest.types("book").source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("总条数:" + search.getHits().getTotalHits());
        System.out.println("最大分数:" + search.getHits().getMaxScore());

        SearchHit[] hits = search.getHits().getHits();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        for (SearchHit hit : hits) {
            String jsonStr = hit.getSourceAsString();
            Book book = gson.fromJson(jsonStr, Book.class);

            System.out.println(jsonStr);
            System.out.println(book);
        }
    }


    //查询2
    @Test
    public void searchTest1() throws IOException {

        SearchRequest searchRequest = new SearchRequest("ems");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("content", "spring"))
                .from(1)
                .size(3)
                .postFilter(QueryBuilders.rangeQuery("age").lte(100))
                .highlighter(new HighlightBuilder().field("*").requireFieldMatch(false))
                .sort("age", SortOrder.DESC);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("总条数:" + searchResponse.getHits().getTotalHits());
        System.out.println("最大分数:" + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        for (SearchHit hit : hits) {
            String jsonStr = hit.getSourceAsString();
//            Book book = gson.fromJson(jsonStr, Book.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            System.out.println(highlightFields.get("content").fragments()[0].toString());

            System.out.println(jsonStr);
//            System.out.println(book);
        }
    }

    //修改
    @Test
    public void testUpdateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("star", "book", "IibESXQBcAJaE7ya0YpY");
        Book book = new Book();
        book.setAge(70).setAddress("中国");
        Gson gson = new Gson();
        updateRequest.doc(gson.toJson(book), XContentType.JSON);
        UpdateResponse update = this.restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    //批量操作
    @Test
    public void testBulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        IndexRequest indexRequest = new IndexRequest("star", "book", "10");
        Book book1 = new Book();
        book1.setAge(10).setAddress("佛山").setContent("三水受到了看风景卢卡斯解放立刻撒旦").setBir(new Date()).setName("老i在").setId("20");
        indexRequest.source(gson.toJson(book1), XContentType.JSON);

        UpdateRequest updateRequest = new UpdateRequest("star", "book", "10");
        Book book2 = new Book();
        book2.setName("星宇").setAge(55);
        updateRequest.doc(gson.toJson(book2), XContentType.JSON);

        DeleteRequest deleteRequest = new DeleteRequest("star", "book", "7k5uTnQBc5vIB42kWm3z");

        bulkRequest.add(indexRequest).add(updateRequest).add(deleteRequest);

        BulkResponse bulk = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        BulkItemResponse[] items = bulk.getItems();
        for (BulkItemResponse item : items) {
            System.out.println(item.status());
        }
    }

    //查询所有
    @Test
    public void testSearchAll() throws IOException {

        SearchRequest searchRequest = new SearchRequest("star");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder).types("book");

        SearchResponse search = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("最大分数:" + search.getHits().getMaxScore());
        System.out.println("搜索的数据总数:" + search.getHits().getTotalHits());

        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }

    }

}
