package com.ggs.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author Starbug
 * @Date 2020/9/3 16:50
 * @Document用在类上,作用: 将Emp的对象映射成ES中一条json格式文档
 * indexName:用来指定这个对象转为json文档存入的文档的名字 要求:ES服务器之前不能存在此索引名
 * type     :用来指定在当前这个索引下创建的类型名称
 */
@Data
@Document(indexName = "company", type = "emp")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Emp {
    @Id //该注解标注的属性与es中文档的_id一一对应
    private String id;
    //用在属性上,代表mapping中一个属性,一个字段 type: 属性,用来指定字段类型
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    @Field(type = FieldType.Integer)
    private Integer age;
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bir;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;
}
