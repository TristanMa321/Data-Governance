package com.atguigu.dga.assess.assessor.quality;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**
 前一天(数据)的产出时效，超过前{days}天产出时效平均值n%。则给0分，其余10分
 */
//@Component("TABLE_PRODUCE_EFFICIENCY")
public class CheckTableProduceEfficiency extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {


    }
}
