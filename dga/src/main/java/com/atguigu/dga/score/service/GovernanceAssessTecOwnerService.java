package com.atguigu.dga.score.service;

import com.atguigu.dga.score.bean.GovernanceAssessTecOwner;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 技术负责人治理考评表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
public interface GovernanceAssessTecOwnerService extends IService<GovernanceAssessTecOwner> {
    void calOwnnerScore(String assessDate);
}
