package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 一张表距离考评日期已经{days}天内没有访问 则给0分 ， 其余给10
    常用的时间日期API:
        java.util.xxxx:  1.8之前就有，老的API。
            特点：   都是实例方法。
            不是线程安全的。不能作为属性。作为方法的局部变量可以。
            日期： Date

        java.time.xxxx:  1.8之后才有，新的API。
            特点：   全是静态方法。
            也是线程安全，效率高。
            日期时间: LocalDateTime
            日期: LocalDate

 */

@Component("LONGTERM_NO_ACCESS")
public class LongTermNoAccess extends AssessorTemplate
{
    /*
                 days = 3
         |
         | 最近一次访问日期:  2023-12-01 table_last_access_time
         |  assessDate - days = 2024-01-28   符合条件的极限时间
         | 考评日期=2024-01-31
         |
        \/
        判断：  table_last_access_time < 极限日期，给0分

     */
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        // 取参数
        TableMetaInfo metaInfo = param.getMetaInfo();
        Timestamp tableLastAccessTime = metaInfo.getTableLastAccessTime();
        Integer days = getIntegerValue(param.getMetric(), "days");
        // 将数据库的时间转换为java能处理的时间 这里是转换成了java.time包下的LocalDateTime
        LocalDateTime tLastAT = LocalDateTime.ofInstant(Instant.ofEpochMilli(tableLastAccessTime.getTime()), ZoneId.systemDefault());
        // 获取考评日期, 把日期字符串转换为日期对象
        LocalDate assessDate = LocalDate.parse(param.getAssessDate());
        // 2024-01-28 等价于 2024-01-28 0:0:0
        LocalDateTime limitLAT = assessDate.minusDays(days).atStartOfDay();
        // 比较
        if (tLastAT.isBefore(limitLAT)){
            setScore(assessDetail, BigDecimal.ZERO, "长时间未访问",
                    "上次访问日期为："+tLastAT+",已经距离考评日期:"+assessDate+",超过了"+days+"天"
                    ,false,null);
        }
    }
}
