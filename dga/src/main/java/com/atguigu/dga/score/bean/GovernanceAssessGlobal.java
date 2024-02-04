package com.atguigu.dga.score.bean;

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
 * 治理总考评表
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("governance_assess_global")
public class GovernanceAssessGlobal implements Serializable {

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
     * 规范分数(百分制)
     */
    private BigDecimal scoreSpec;

    /**
     * 存储分数(百分制)
     */
    private BigDecimal scoreStorage;

    /**
     * 计算分数(百分制)
     */
    private BigDecimal scoreCalc;

    /**
     * 质量分数(百分制)
     */
    private BigDecimal scoreQuality;

    /**
     * 安全分数(百分制)
     */
    private BigDecimal scoreSecurity;

    /**
     * 加权分数
     */
    private BigDecimal score;

    /**
     * 问题表数量
     */
    private Long tableNum;

    /**
     * 问题项个数
     */
    private Long problemNum;

    /**
     * 创建时间
     */
    private Timestamp createTime;
}
