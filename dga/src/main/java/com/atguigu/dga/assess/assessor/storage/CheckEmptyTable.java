package com.atguigu.dga.assess.assessor.storage;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**

 是否空表，空表则0分 ，有数据则10分
 */
@Component("IS_EMPTY_TABLE")
public class CheckEmptyTable extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        TableMetaInfo metaInfo = param.getMetaInfo();

        if (metaInfo.getTableSize() == 0){
            setScore(assessDetail, BigDecimal.ZERO, "空表","",false,null);
        }
    }
}
