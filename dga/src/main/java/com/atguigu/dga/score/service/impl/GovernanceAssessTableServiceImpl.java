package com.atguigu.dga.score.service.impl;

import com.atguigu.dga.score.bean.GovernanceAssessTable;
import com.atguigu.dga.score.bean.GovernanceType;
import com.atguigu.dga.score.mapper.GovernanceAssessTableMapper;
import com.atguigu.dga.score.service.GovernanceAssessTableService;
import com.atguigu.dga.score.service.GovernanceTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 表治理考评情况 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
@Service
public class GovernanceAssessTableServiceImpl extends ServiceImpl<GovernanceAssessTableMapper, GovernanceAssessTable> implements GovernanceAssessTableService {

    @Autowired
    private GovernanceTypeService typeService;
    @Override
    public void calTableScore(String db, String assessDate) {
        // 为了保证幂等性， 先删
        remove(new QueryWrapper<GovernanceAssessTable>().eq("assess_date",assessDate).eq("schema_name",db));
        // 调用mapper计算分数
        List<GovernanceAssessTable> scores = baseMapper.calScore(db, assessDate);
        // 还缺少加权后的分数
        // 查询权重信息
        List<GovernanceType> typeList = typeService.list();
        /*
            根governance_type字段对应
            key: type_code
            value: type_weight
         */
        Map<String, BigDecimal> weightMap = new HashMap<>();
        for (GovernanceType governanceType : typeList) {
            weightMap.put(governanceType.getTypeCode(), governanceType.getTypeWeight());
        }
        // 计算加权分数
        for (GovernanceAssessTable score : scores) {
            BigDecimal weightScore = BigDecimal.ZERO;
            score.setScoreOnTypeWeight(
                    score.getScoreCalcAvg().multiply(weightMap.get("CALC"))
                            .add(score.getScoreQualityAvg().multiply(weightMap.get("QUALITY")))
                            .add(score.getScoreStorageAvg().multiply(weightMap.get("STORAGE")))
                            .add(score.getScoreSecurityAvg().multiply(weightMap.get("SECURITY")))
                            .add(score.getScoreSpecAvg().multiply(weightMap.get("SPEC")))
                            .movePointLeft(1)
            );
        }
        // 写入数据库
        saveBatch(scores);

    }
}
