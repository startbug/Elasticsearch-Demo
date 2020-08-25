package com.ggs.elasticsearch;

import org.apache.http.entity.ContentType;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * @Author Starbug
 * @Date 2020/8/25 18:07
 */

public class TestIndexAndTypeMapping {

    private TransportClient transportClient;

    @Before
    public void before() throws UnknownHostException {
        //创建客户端
        this.transportClient = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es服务器地址
        transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.232.130"), 9300));
    }

    @After
    public void after() {
        this.transportClient.close();
    }

    //创建索引
    @Test
    public void testCreateIndex() {
        //创建一个索引,保证索引不存在
        CreateIndexResponse createIndexResponse = transportClient.admin().indices().prepareCreate("star").get();
        //获取信息
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    //删除索引
    @Test
    public void testDeleteIndex() {
        //删除索引
        AcknowledgedResponse acknowledgedResponse = this.transportClient.admin().indices().prepareDelete("star").get();
        System.out.println(acknowledgedResponse.isAcknowledged());
    }

    //[了解] 创建索引 创建类型 创建mapping
    @Test
    public void testCreateIndexAndTypeMapping() throws ExecutionException, InterruptedException {
        //创建一个索引请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("star");

        /**
         * 索引设置类型
         * 参数1: 类型名
         * 参数2: 映射的对应格式字符串
         * 参数3: 映射格式类型
         */
        String str = "{\"properties\":{\"name\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"age\":{\"type\":\"integer\"},\"bir\":{\"type\":\"date\"},\"content\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"address\":{\"type\":\"keyword\"}}}";
        createIndexRequest.mapping("book", str, XContentType.JSON);

        //创建一个索引对象
        CreateIndexResponse createIndexResponse = this.transportClient.admin().indices().create(createIndexRequest).get();

        //创建索引
        System.out.println(createIndexResponse.isAcknowledged());

    }

}
