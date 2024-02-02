package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 一张表{days}天内没有产出数据  则给0分，其余给10
 */
@Component("LONGTERM_NO_PRODUCE")
public class LongTermNoProduce extends AssessorTemplate
{
    /*
        如果一张表今天产出了数据，此时这张表（hive中的）的table_last_modify_time会被更新
        思路： 考评日期-最后修改时间 ? {days}
     */

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws ParseException {
        // 取参数
        TableMetaInfo metaInfo = param.getMetaInfo();
        Timestamp tableLastModifyTime = metaInfo.getTableLastModifyTime();
        Integer days = getIntegerValue(param.getMetric(), "days");
        // 获取考评日期, 把日期字符串转换为日期对象
        Date assessDate = DateUtils.parseDate(param.getAssessDate(), "yyyy-MM-dd");
        // .getTime:获取时间戳类型, 单位是毫秒
        long diffMs = Math.abs(assessDate.getTime() - tableLastModifyTime.getTime());
        // 时间单位转换，把毫秒转成天
        long diffDays = TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);
        if (diffDays > days){
            setScore(assessDetail, BigDecimal.ZERO, "长期未产出", "上一次产出的时间:"+
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(tableLastModifyTime.getTime()), ZoneId.systemDefault())
                    + " 距离考评日期："+param.getAssessDate() + " 已经超过了阈值："+ days+"天",false,null);

        }
    }
}
