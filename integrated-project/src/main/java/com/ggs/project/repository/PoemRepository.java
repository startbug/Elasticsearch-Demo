package com.ggs.project.repository;

import com.ggs.project.entity.Poem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author Starbug
 * @Date 2020/9/6 14:46
 */
public interface PoemRepository extends ElasticsearchRepository<Poem, String> {

}
