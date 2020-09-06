package com.ggs.project.service.impl;

import com.ggs.project.entity.Category;
import com.ggs.project.mapper.CategoryMapper;
import com.ggs.project.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Starbug
 * @since 2020-09-04
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
