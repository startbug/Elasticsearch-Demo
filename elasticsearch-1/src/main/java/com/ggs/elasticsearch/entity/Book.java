package com.ggs.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * @Author Starbug
 * @Date 2020/8/25 19:18
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Book {
    private String id;
    private String name;
    private int age;
    private Date bir;
    private String content;
    private String address;

}
