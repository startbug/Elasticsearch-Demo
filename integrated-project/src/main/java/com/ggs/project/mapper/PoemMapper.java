package com.ggs.project.mapper;

import com.ggs.project.entity.Poem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Starbug
 * @since 2020-09-04
 */
public interface PoemMapper extends BaseMapper<Poem> {

    List<Poem> findAllResult();
}
