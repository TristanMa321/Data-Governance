package com.atguigu.dga.assess.assessor.storage;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**
 同层次两个表字段重复(字段名+注释)超过{percent}%，则给0分，其余给10分
 */
//@Component("TABLE_SIMILAR")
public class CheckSimiliarTable extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {

    }
}
