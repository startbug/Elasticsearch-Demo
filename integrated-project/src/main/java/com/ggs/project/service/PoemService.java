package com.ggs.project.service;

import com.ggs.project.entity.Poem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Starbug
 * @since 2020-09-04
 */
public interface PoemService extends IService<Poem> {

    void saveAll();

    void deleteAllDoc();

    List<Poem> findByKeywords(String content, String type, String author);
}
