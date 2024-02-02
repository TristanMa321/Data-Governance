package com.atguigu.dga.assess.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2024-01-30
 * 这个是metaInfo各个表每个字段的信息！！！，对应表中col_name_json列
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    private String comment;
    private String name;
    private String type;
    private Boolean setComment;
    private Boolean setName;
    private Boolean setType;
}
