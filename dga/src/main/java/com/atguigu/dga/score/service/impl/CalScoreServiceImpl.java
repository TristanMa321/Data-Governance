package com.atguigu.dga.score.service.impl;

import com.atguigu.dga.score.service.CalScoreService;
import com.atguigu.dga.score.service.GovernanceAssessGlobalService;
import com.atguigu.dga.score.service.GovernanceAssessTableService;
import com.atguigu.dga.score.service.GovernanceAssessTecOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2024-02-04
 */
@Service
public class CalScoreServiceImpl implements CalScoreService {
    @Autowired
    private GovernanceAssessTableService tableService;
    @Autowired
    private GovernanceAssessTecOwnerService ownerService;
    @Autowired
    private GovernanceAssessGlobalService globalService;

    @Override
    public void calScore(String db, String date) {
        // 先计算每张表的得分
        tableService.calTableScore(db,date);
        // 计算每个人和全局得分
        ownerService.calOwnnerScore(date);
        globalService.calGlobalScore(date);
    }
}
