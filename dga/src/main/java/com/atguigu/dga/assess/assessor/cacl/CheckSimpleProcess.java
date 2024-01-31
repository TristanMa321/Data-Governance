package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**

 sql语句没有任何join|groupby|union all，且where过滤中没有非分区字段。符合以上情况给0分，其余给10分。

 */
//@Component("SIMPLE_PROCESS")
public class CheckSimpleProcess extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws Exception {

    }
}
