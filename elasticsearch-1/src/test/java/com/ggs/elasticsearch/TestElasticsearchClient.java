package com.ggs.elasticsearch;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class TestElasticsearchClient {
//    {"properties":{"name":{"type":"text","analyzer":"ik_max_word"},"age":{"type":"integer"},"bir":{"type":"date"},"content":{"type":"text","analyzer":"ik_max_word"},"address":{"type":"keyword"}}}


    @Test
    public void initTransportCient() throws UnknownHostException {
        //创建客户端
        TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es服务器地址
        transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.232.130"), 9300));
        transportClient.close();
    }


}
