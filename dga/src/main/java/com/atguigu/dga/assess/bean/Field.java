package com.atguigu.dga.assess.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2024-01-30
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
