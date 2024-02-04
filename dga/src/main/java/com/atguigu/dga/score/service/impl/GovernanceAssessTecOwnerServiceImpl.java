package com.atguigu.dga.score.service.impl;

import com.atguigu.dga.score.bean.GovernanceAssessTable;
import com.atguigu.dga.score.bean.GovernanceAssessTecOwner;
import com.atguigu.dga.score.mapper.GovernanceAssessTecOwnerMapper;
import com.atguigu.dga.score.service.GovernanceAssessTecOwnerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 技术负责人治理考评表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
@Service
public class GovernanceAssessTecOwnerServiceImpl extends ServiceImpl<GovernanceAssessTecOwnerMapper, GovernanceAssessTecOwner> implements GovernanceAssessTecOwnerService {

    @Override
    public void calOwnnerScore(String assessDate) {
        // 为了保证幂等性， 先删
        remove(new QueryWrapper<GovernanceAssessTecOwner>().eq("assess_date",assessDate));
        // 调用mapper计算分数
        List<GovernanceAssessTecOwner> scores = baseMapper.calScore(assessDate);
        // 写入数据库
        saveBatch(scores);
    }
}
