package com.ggs.elasticsearch.repository;

import com.ggs.elasticsearch.entity.Emp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author Starbug
 * @Date 2020/9/3 17:03
 * 自定义EmpRepository
 */
public interface EmpElasticsearchDao extends ElasticsearchRepository<Emp, String> {

    List<Emp> findByName(String name);

    List<Emp> findByNameAndAge(String name, Integer age);

    List<Emp> findByAgeGreaterThan(Integer age);
}
