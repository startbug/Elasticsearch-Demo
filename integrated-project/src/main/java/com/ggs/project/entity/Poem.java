package com.ggs.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * <p>
 *
 * </p>
 *
 * @author Starbug
 * @since 2020-09-04
 */
@Data
@TableName("t_poem")
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "poems", type = "poem")
public class Poem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String href;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String authordes;

    @Field(type = FieldType.Keyword)
    private String origin;

    //Nested告诉ES当前是一个对象
    @Field(type = FieldType.Nested)
    @TableField(select = false)
    private Category category = new Category();


}
