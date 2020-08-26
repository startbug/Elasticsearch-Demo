package com.ggs.elasticsearch;

import com.ggs.elasticsearch.entity.Emp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @Author Starbug
 * @Date 2020/8/26 15:56
 */
public class TestDocumentHighlightQuery {

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
    public void testHighlightSearch() {
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("*")
                .requireFieldMatch(false) //false: 如果其他字段有匹配的词,也显示高亮
                .preTags("<span style='color:red'>")
                .postTags("</span>");

        SearchResponse searchResponse = this.transportClient.prepareSearch("ems")
                .setTypes("emp")
                .setQuery(QueryBuilders.multiMatchQuery("我喜欢框架课程", "name", "content"))
                .highlighter(highlightBuilder) //高亮处理
                .get();

        System.out.println("总条数:" + searchResponse.getHits().getTotalHits());
        System.out.println("最大分数:" + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        for (SearchHit hit : hits) {
            System.out.println("-------------------------------------------------");
            String jsonStr = hit.getSourceAsString();
            Emp emp = gson.fromJson(jsonStr, Emp.class);
            System.out.println(emp);

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("name")) {
                String name = highlightFields.get("name").fragments()[0].toString();
                emp.setName(name);
            }
            if (highlightFields.containsKey("content")) {
                String content = highlightFields.get("content").fragments()[0].toString();
                emp.setContent(content);
            }
            System.out.println(emp);
            System.out.println("-------------------------------------------------");
        }


    }


}
