package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**
 一张表距离考评日期已经{days}天内没有访问 则给0分 ， 其余给10
 */
//@Component("LONGTERM_NO_ACCESS")
public class LongTermNoAccess extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {

    }
}
