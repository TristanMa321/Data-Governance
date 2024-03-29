package com.atguigu.dga.assess.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 治理考评结果明细
 * </p>
 *
 * @author atguigu
 * @since 2024-01-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("governance_assess_detail")
public class GovernanceAssessDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考评日期
     */
    private String assessDate;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 库名
     */
    private String schemaName;

    /**
     * 指标项id
     */
    private Long metricId;

    /**
     * 指标项名称
     */
    private String metricName;

    /**
     * 治理类型
     */
    private String governanceType;

    /**
     * 技术负责人
     */
    private String tecOwner;

    /**
     * 考评得分
     */
    private BigDecimal assessScore;

    /**
     * 考评问题项
     */
    private String assessProblem;

    /**
     * 考评备注
     */
    private String assessComment;

    /**
     * 考评是否异常
     */
    private String isAssessException;

    /**
     * 异常信息
     */
    private String assessExceptionMsg;

    /**
     * 治理处理路径
     */
    private String governanceUrl;

    /**
     * 创建日期
     */
    private Timestamp createTime;
}
