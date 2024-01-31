package com.atguigu.dga.assess.assessor.storage;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**

 是否空表，空表则0分 ，有数据则10分
 */
//@Component("IS_EMPTY_TABLE")
public class CheckEmptyTable extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {

    }
}
