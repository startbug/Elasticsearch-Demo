package com.ggs.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Starbug
 * @Date 2020/8/26 14:54
 */
public class TestDocumentSearch {

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


    @Test
    public void testQuery(){
        //查询所有
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = this.transportClient.prepareSearch("ems")
                .setTypes("emp")
                .setQuery(matchAllQueryBuilder)
                .setFrom(2) //起始位置, 第几条(默认从0开始)
                .setSize(2) //设置每页展示数
                .addSort("age", SortOrder.ASC) //设置排序
                .get();

        System.out.println("总条数"+searchResponse.getHits().getTotalHits());
        System.out.println("最高得分:"+searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void testDivide(){
        //查询所有
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        //termQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("content", "框架");
//        this.testResult(termQueryBuilder);

        //rangeQuery
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").gte(0).lte(23);
//        this.testResult(rangeQueryBuilder);

        //wildcardQuery 通配符 ?表示一个 *表示0或多个  搜索(ik)分词后的结果 "框?"匹配框架  但是"框架?"没有匹配到任何的词,所以搜索不到
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("content", "框架?");
//        this.testResult(wildcardQueryBuilder);

        //prefixQuery前缀查询
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("content", "框");
//        this.testResult(prefixQueryBuilder);

        //ids查询
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds("tZZgKXQBYbBwDPDOlp8x", "sJZgKXQBYbBwDPDOlp8x");
//        this.testResult(idsQueryBuilder);

        //fuzzy查询  0-2不允许模糊  3-5 可以出现一个模糊  >5最多允许两个模糊
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("content", "soriog");
//        this.testResult(fuzzyQueryBuilder);

        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("content", "spring"));
        this.testResult(boolQueryBuilder);


    }


    public void testResult(QueryBuilder queryBuilder){
        //查询所有
        SearchResponse searchResponse = this.transportClient.prepareSearch("ems")
                .setTypes("emp")
                .setQuery(queryBuilder)
                .setFrom(0) //起始位置, 第几条(默认从0开始)
                .setSize(20) //设置每页展示数
                .addSort("age", SortOrder.ASC) //设置排序
                .setSource(SearchSourceBuilder.searchSource().fetchSource("*","name")) //执行结果中返回指定字段,排查指定字段
                .get();

        System.out.println("总条数"+searchResponse.getHits().getTotalHits());
        System.out.println("最高得分:"+searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

}
