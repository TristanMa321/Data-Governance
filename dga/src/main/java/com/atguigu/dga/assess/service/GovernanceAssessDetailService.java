package com.atguigu.dga.assess.service;

import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 治理考评结果明细 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-30
 */
public interface GovernanceAssessDetailService extends IService<GovernanceAssessDetail> {
    void generateAssessDetail(String db, String assessDate) throws ExecutionException, InterruptedException;
}
