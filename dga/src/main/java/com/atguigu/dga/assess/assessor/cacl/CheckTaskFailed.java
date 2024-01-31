package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**

 检查某张表在DS 当天调度中是否有报错。有则给0分，否则给10分。

 */
//@Component("TASK_FAILED")
public class CheckTaskFailed extends AssessorTemplate
{


    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {

    }
}
