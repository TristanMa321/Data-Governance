package com.atguigu.dga.assess.assessor.security;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 未设置 0分  其余10分
 */
@Component("IS_SAFE_LEVEL_SET")
public class CheckSecurityLevel extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        TableMetaInfo metaInfo = param.getMetaInfo();
        TableMetaInfoExtra metaInfoExtra = metaInfo.getTableMetaInfoExtra();
        String securityLevel = metaInfoExtra.getSecurityLevel();
        /*
            所属包：apache.common.lang3
            StringUtils.isBlank(String s): 如果s是null或''或是白字符，返回true。
                白字符： 看不见的字符。看起来是一片空白的字符。空格，回车，tab缩进
        */
        if (StringUtils.isBlank(securityLevel) || MetaConstant.SECURITY_LEVEL_UNSET.equals(securityLevel)){
            setScore(assessDetail, BigDecimal.ZERO, "缺少安全级别","",true,metaInfo.getId());
        }

    }
}
