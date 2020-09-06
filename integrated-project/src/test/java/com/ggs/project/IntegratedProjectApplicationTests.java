package com.ggs.project;

import com.ggs.project.entity.Poem;
import com.ggs.project.mapper.PoemMapper;
import com.ggs.project.service.PoemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class IntegratedProjectApplicationTests {

    @Autowired
    private PoemMapper poemMapper;

    @Autowired
    private PoemService poemService;

    @Test
    void contextLoads() {
        List<Poem> poems = poemService.list();
        poems.forEach(poem -> System.out.println(poem));
    }

    @Test
    public void test1() {
        poemMapper.findAllResult().forEach(System.out::println);
    }

}
