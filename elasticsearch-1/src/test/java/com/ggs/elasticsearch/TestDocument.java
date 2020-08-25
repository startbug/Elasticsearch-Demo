package com.ggs.elasticsearch;

import com.ggs.elasticsearch.entity.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

/**
 * @Author Starbug
 * @Date 2020/8/25 19:18
 */
public class TestDocument {
    TransportClient transportClient;

    @Before
    public void before() throws UnknownHostException {
        //创建客户端
        this.transportClient = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es服务地址
        transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.232.130"), 9300));
    }

    @After
    public void after() {
        this.transportClient.close();
    }

    //添加一个文档,指定id
    @Test
    public void testCreateDoc1() {
        Book book = new Book("1", "新52", 100, new Date(), "大多数开了房间数量科技的发达里卡多就是洒到了沃厄瑞哦阿文u阿三i", "佛山");
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        //转换为json
        String jsonStr = gson.toJson(book);
        System.out.println(jsonStr);
        //索引一条文档
        IndexResponse indexResponse = this.transportClient.prepareIndex("star", "book", book.getId()).setSource(jsonStr, XContentType.JSON).get();

        System.out.println(indexResponse.status());
    }


    //添加一条文档,自动生成id
    @Test
    public void testCreateDoc2() {

        Book book = new Book("1", "高科技", 100, new Date(), "大多数开了无论如何房间数量科技的发达里卡多就是洒到了沃厄瑞哦阿文u阿三i", "佛山");

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String jsonStr = gson.toJson(book);

        //索引一条文档
        IndexResponse indexResponse = this.transportClient.prepareIndex("star", "book").setSource(jsonStr, XContentType.JSON).get();

        System.out.println(indexResponse.status()); //Created

    }

    //更新一条文档
    @Test
    public void testUpdateDoc() {
        Book book = new Book();
        book.setAddress("广州");
        book.setName("guardian");

        Gson gson = new Gson();
        String jsonStr = gson.toJson(book);


        UpdateResponse updateResponse = this.transportClient.prepareUpdate("star", "book", "1").setDoc(jsonStr, XContentType.JSON).get();
        System.out.println(updateResponse.status());  //OK
    }

    //删除一条文档
    @Test
    public void testDeleteDoc() {
        DeleteResponse deleteResponse = this.transportClient.prepareDelete("star", "book", "zSX0JXQBjHecUtYaqhQr").get();
        System.out.println(deleteResponse.status());  //OK
    }

    //查询一条文档
    @Test
    public void testFindOne() throws InvocationTargetException, IllegalAccessException {
        GetResponse getResponse = this.transportClient.prepareGet("star", "book", "1").get();
        String jsonStr = getResponse.getSourceAsString();
        System.out.println(jsonStr);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        Book book = gson.fromJson(jsonStr, Book.class);
        System.out.println(book);
    }

    //查询所有
    @Test
    public void testSearchAll(){
        SearchResponse searchResponse = this.transportClient.prepareSearch("star")
                .setTypes("book") //指定类型
                .setQuery(QueryBuilders.matchAllQuery()) //指定查询条件
                .get(); //执行查询
        System.out.println("总条数:"+searchResponse.getHits().getTotalHits());
        System.out.println("最大得分:"+searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    //term查询
    @Test
    public void testQuery(){
        SearchResponse searchResponse = this.transportClient.prepareSearch("star")
                .setTypes("book")
                .setQuery(QueryBuilders.termQuery("content", "无论如何"))
                .get();
        System.out.println("总条数:"+searchResponse.getHits().getTotalHits());
        System.out.println("最大得分:"+searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }
}
