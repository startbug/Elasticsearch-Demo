package com.ggs.project.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Starbug
 * @Date 2020/9/6 16:02
 */
@RestController
@RequestMapping("/dic")
public class DicController {

    @Value(value = "${starbug.remote-dic}")
    private String remoteDictPath;

    //添加热词到远程词典
    @RequestMapping("/save/hot/dict")
    public Map<String, Object> saveHotDict(String keyword, HttpServletRequest request) {
        HashMap<String, Object> map = Maps.newHashMap();
        String trimAllWhitespace = StringUtils.trimAllWhitespace(keyword);
        String realPath = request.getServletContext().getRealPath("/");
        BufferedReader bufferedReader = null;
        try {
            //读取文件
            File file = new File(realPath, remoteDictPath);
            String wholeFileStr = FileUtils.readFileToString(file);
            bufferedReader = new BufferedReader(new FileReader(file));
            if (!wholeFileStr.contains(trimAllWhitespace)) {
                //将新的热词写入远程词典中
                FileUtils.write(file, trimAllWhitespace + "\r\n", "UTF-8", true);
                map.put("success", true);
                map.put("msg", "添加成功");
            } else {
                throw new RuntimeException("关键词已经存在,无需重复添加!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @RequestMapping("findAllDic")
    public List<String> findAllDic(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("/");
        File file = new File(realPath, remoteDictPath);
        List<String> dictList = Lists.newArrayList();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            dictList = FileUtils.readLines(file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dictList;
    }

    @RequestMapping("/delete/dict")
    public Map<String, Object> deleteDict(String keyword, HttpServletRequest request) {
        HashMap<String, Object> map = Maps.newHashMap();
        String realPath = request.getServletContext().getRealPath("/");
        File file = new File(realPath, remoteDictPath);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
            List<String> strings = FileUtils.readLines(file);
            Set<String> collect = strings.stream().collect(Collectors.toSet());
            collect.remove(keyword);
            FileUtils.writeLines(file, "UTF-8", collect, null, false);
            map.put("success", true);
            map.put("msg", "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "删除失败:" + e.getMessage());
        }
        return map;

    }

}
