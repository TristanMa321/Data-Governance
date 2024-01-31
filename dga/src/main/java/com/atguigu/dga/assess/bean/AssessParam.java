package com.atguigu.dga.assess.bean;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2024-01-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessParam {
    private String assessDate;

    private TableMetaInfo metaInfo;

    private GovernanceMetric metric;

}
