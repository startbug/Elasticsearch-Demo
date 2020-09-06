package com.ggs.project.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author Starbug
 * @Date 2020/9/6 13:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PageBean<T> {

    private Long total; //总页数
    private Long records; //总条数
    private Integer page; //当前页数
    private List<T> rows; //当前页数据

}
