package com.atguigu.dga.assess.assessor.quality;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;

import java.io.IOException;

/**
 必须日分区表
 前一天数据产出的数据量，超过前days天平均产出量{upper_limit}% ，或低于{lower_limit}%  ，则给0分，其余10分
 */
//@Component("TABLE_PRODUCT_VOLUME_MONITOR")
public class CheckTableProduceVolume extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws IOException {

    }

}
