package com.atguigu.dga.assess.assessor.spec;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 表是否有备注。有则10分，无则0分
 */
@Component("HAVE_TABLE_COMMENT")
public class CheckIfHadComment extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        TableMetaInfo metaInfo = param.getMetaInfo();

        /*
            所属包：apache.common.lang3
            StringUtils.isBlank(String s): 如果s是null或''或是白字符，返回true。
                白字符： 看不见的字符。看起来是一片空白的字符。空格，回车，tab缩进
        */
        if (StringUtils.isBlank(metaInfo.getTableComment())){
            setScore(assessDetail, BigDecimal.ZERO, "表缺少备注","",false,null);
        }
    }
}
