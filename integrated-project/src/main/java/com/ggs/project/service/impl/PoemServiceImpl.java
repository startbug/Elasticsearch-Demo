package com.ggs.project.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggs.project.entity.Poem;
import com.ggs.project.mapper.PoemMapper;
import com.ggs.project.repository.PoemRepository;
import com.ggs.project.service.PoemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Starbug
 * @since 2020-09-04
 */
@Service
public class PoemServiceImpl extends ServiceImpl<PoemMapper, Poem> implements PoemService {

    @Autowired
    private PoemMapper poemMapper;

    @Autowired
    private PoemRepository poemRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void saveAll() {
        //清空索引库
        poemRepository.deleteAll();
        //从数据库中拿出所有的数据
        List<Poem> poemList = poemMapper.findAllResult();
        //将所有数据保存到es中
        poemRepository.saveAll(poemList);
    }

    @Override
    public void deleteAllDoc() {
        poemRepository.deleteAll();
    }

    @Override
    public List<Poem> findByKeywords(String content, String type, String author) {
        List<Poem> poemList = null;
        try {
            SearchRequest searchRequest = new SearchRequest("poems");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            if (StringUtils.isBlank(content)) {
                sourceBuilder.query(QueryBuilders.matchAllQuery());
            } else {
                sourceBuilder.query(QueryBuilders.multiMatchQuery(content, "name", "content", "author", "authordes"));
            }

            //指定过滤条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (StringUtils.isNoneBlank(type)) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("type", type));
            }
            if (StringUtils.isNoneBlank(author)) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("author", author));
            }
            sourceBuilder.postFilter(boolQueryBuilder);

            //设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("*").preTags("<span style='color:red'>").postTags("</span>").requireFieldMatch(false);
            sourceBuilder.highlighter(highlightBuilder);

            //置顶显示条数
            sourceBuilder.size(10);

            //指定搜索类型
            searchRequest.types("poem").source(sourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            ObjectMapper objectMapper = new ObjectMapper();

            //获取返回结果
            if (searchResponse.getHits().getTotalHits() > 0) {
                poemList = new ArrayList<>();
            }
            SearchHit[] hits = searchResponse.getHits().getHits();
            for (SearchHit hit : hits) {
                String jsonStr = hit.getSourceAsString();
                Poem poem = objectMapper.readValue(jsonStr, Poem.class);

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields.containsKey("name")) {
                    String nameHighlight = highlightFields.get("name").fragments()[0].toString();
                    poem.setName(nameHighlight);
                }
                if (highlightFields.containsKey("content")) {
                    String contentHighlight = highlightFields.get("content").fragments()[0].toString();
                    poem.setContent(content);
                }
                if (highlightFields.containsKey("author")) {
                    String authorHighlight = highlightFields.get("author").fragments()[0].toString();
                    poem.setName(authorHighlight);
                }
                if (highlightFields.containsKey("authordes")) {
                    String authordesHighlight = highlightFields.get("authordes").fragments()[0].toString();
                    poem.setName(authordesHighlight);
                }
                poemList.add(poem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poemList;
    }
}
