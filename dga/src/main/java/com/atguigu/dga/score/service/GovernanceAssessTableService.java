package com.atguigu.dga.score.service;

import com.atguigu.dga.score.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 表治理考评情况 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
public interface GovernanceAssessTableService extends IService<GovernanceAssessTable> {
    // 计算每张表的考评得分
    void calTableScore(String db,String assessDate);
}
