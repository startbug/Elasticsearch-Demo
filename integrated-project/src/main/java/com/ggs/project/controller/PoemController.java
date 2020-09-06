package com.ggs.project.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ggs.project.entity.Poem;
import com.ggs.project.entity.vo.PageBean;
import com.ggs.project.service.PoemService;
import com.github.houbb.segment.support.segment.result.impl.SegmentResultHandlers;
import com.github.houbb.segment.util.SegmentHelper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Starbug
 * @since 2020-09-04
 */
@Controller
@RequestMapping("/poem")
@Slf4j
public class PoemController {

    @Autowired
    private PoemService poemService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @RequestMapping("/test")
    public String test(HttpServletRequest request) throws IOException {
//        String property = System.getProperty("user.dir");
        String realPath = request.getServletContext().getRealPath("/");
//        System.out.println(realPath);
//
//        System.out.println(property);
        File file = new File(realPath, "es/dict/dic.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
//        }

        }
        return file.getAbsolutePath();
    }

    @ResponseBody
    @RequestMapping("/findByPage")
    public PageBean<Poem> findByPage(Integer page, Integer rows) {
        Page<Poem> pageRows = poemService.page(new Page<>(page, rows), null);
        PageBean<Poem> pageBean = new PageBean<>();
        pageBean.setPage(page).setRecords(pageRows.getTotal()).setTotal(pageRows.getPages()).setRows(pageRows.getRecords());
        return pageBean;
    }

    @ResponseBody
    @RequestMapping("/saveAllDoc")
    public Map<String, Object> saveAll() {
        log.info("正在执行索引的重建....");
        HashMap<String, Object> map = Maps.newHashMap();
        try {
            poemService.saveAll();
            map.put("success", true);
            map.put("msg", "重建索引成功!");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "重建索引失败:" + e.getMessage());
        }
        return map;
    }


    @ResponseBody
    @RequestMapping("deleteAllDoc")
    public Map<String, Object> deleteAllDoc() {
        log.info("正在执行索引的重建....");
        HashMap<String, Object> map = Maps.newHashMap();
        try {
            poemService.deleteAllDoc();
            map.put("success", true);
            map.put("msg", "成功删除所有文档!");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "删除失败:" + e.getMessage());
        }
        return map;
    }

    @ResponseBody
    @RequestMapping("findAllKeywords")
    public Map<String, Object> findAllKeyWords(String content, String type, String author) {
        HashMap<String, Object> map = Maps.newHashMap();

//        放入redis, 记录搜索的分数,一个词被搜索一次,累加0.5分
        if (StringUtils.isNotBlank(content)) {
            //对搜索内容部分进行分词
            List<String> segment = SegmentHelper.segment(content, SegmentResultHandlers.word());
            log.info("当前搜索的分词结果为:[{}]", segment);
            segment.forEach(word -> {
                stringRedisTemplate.opsForZSet().incrementScore("keywords", word, 0.5);
            });
        }
        if (StringUtils.equals("所有", type)) {
            type = null;
        }
        if (StringUtils.equals("所有", author)) {
            author = null;
        }
        log.info("type:[{}], author:[{}]", type, author);
        try {
            List<Poem> poemList = poemService.findByKeywords(content, type, author);
            map.put("success", true);
            map.put("msg", "查询成功");
            map.put("poems", poemList);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "查询异常");
        }
        return map;
    }


    @ResponseBody
    @RequestMapping("findRedisKeywords")
    public Set<ZSetOperations.TypedTuple<String>> findRedisKeywords() {
        Set<ZSetOperations.TypedTuple<String>> keywords = stringRedisTemplate.opsForZSet().reverseRangeWithScores("keywords", 0, -1);
        System.out.println(keywords);
        return keywords;
    }

}

