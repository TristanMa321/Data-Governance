package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

import java.text.ParseException;

/**
 一张表{days}天内没有产出数据  则给0分，其余给10
 */
//@Component("LONGTERM_NO_PRODUCE")
public class LongTermNoProduce extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws ParseException {

    }
}
