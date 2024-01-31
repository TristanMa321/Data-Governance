package com.atguigu.dga.assess.assessor.storage;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

/**
 未设定周期类型的 给 0分
 周期类型为永久、拉链表 则给10分
 周期类型为日分区 :
 无分区信息的给0分
 没设生命周期给0分
 周期长度超过建议周期天数{days}给5分
 */
//@Component("LIFECYCLE_ELIGIBLE")
public class CheckLifeCycle extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {

    }
}
