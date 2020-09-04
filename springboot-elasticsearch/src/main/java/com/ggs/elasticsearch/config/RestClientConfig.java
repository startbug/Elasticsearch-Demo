package com.ggs.elasticsearch.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @Author Starbug
 * @Date 2020/9/2 18:10
 */
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {


    /**
     * 旧版Elasticsearch整合SpringBoot使用的是ElasticsearchTemplate,底层使用的是TransportClient,es7版本弃用,8版本删除,使用的是tcp连接,端口是9300
     * 新版整合使用restful客户端,9200端口
     *
     * @return
     */
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("192.168.232.130:9201","192.168.232.130:9202","192.168.232.130:9203")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
