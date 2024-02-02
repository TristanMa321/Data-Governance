package com.atguigu.dga.assess.assessor.storage;

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
 未设定周期类型的 给 0分
 周期类型为永久、拉链表 则给10分
 周期类型为日分区 :
 无分区信息的给0分
 没设生命周期给0分
 周期长度超过建议周期天数{days}给5分
 */
@Component("LIFECYCLE_ELIGIBLE")
public class  CheckLifeCycle extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        // 获取参数
        TableMetaInfo metaInfo = param.getMetaInfo();
        Long id = metaInfo.getId();
        TableMetaInfoExtra metaInfoExtra = metaInfo.getTableMetaInfoExtra();
        String lifecycleType = metaInfoExtra.getLifecycleType();
        Long lifecycleDays = metaInfoExtra.getLifecycleDays();
        Integer recommandDays = getIntegerValue(param.getMetric(), "days");

        // 判断是不是设置了声明周期
        // 看课件的流程图就懂了
        if (StringUtils.isBlank(lifecycleType) || MetaConstant.LIFECYCLE_TYPE_UNSET.equals(lifecycleType)){
            setScore(assessDetail, BigDecimal.ZERO, "声明周期设置不合理", "未设置声明周期类型", true, id);
        } else if (MetaConstant.LIFECYCLE_TYPE_DAY.equals(lifecycleType)){
            String partitionColNameJson = metaInfo.getPartitionColNameJson();
            if ("[]".equals(partitionColNameJson)){
                setScore(assessDetail, BigDecimal.ZERO, "声明周期设置不合理", "日分区表缺少分区字段", false, null);
            }
            if (lifecycleDays == null){
                setScore(assessDetail, BigDecimal.ZERO, "声明周期设置不合理", "日分区表未设置生命周期天数", true, id);
            }else if (lifecycleDays > recommandDays){
                setScore(assessDetail, BigDecimal.valueOf(5), "声明周期设置不合理", "日分区表生命周期天数超过建议值"+recommandDays, true, id);
            }
        }

    }
}
