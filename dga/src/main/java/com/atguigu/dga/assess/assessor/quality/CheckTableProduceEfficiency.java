package com.atguigu.dga.assess.assessor.quality;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.TDsTaskInstance;
import com.atguigu.dga.assess.service.TDsTaskInstanceService;
import com.atguigu.dga.config.CacheUtil;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 前一天(数据)的产出时间，超过前{days}天产出时间平均值n%。则给0分，其余10分
 说白了就是一个表数据装载的时间 ds中endTime-startTime
 */
@Component("TABLE_PRODUCE_EFFICIENCY")
public class CheckTableProduceEfficiency extends AssessorTemplate
{
    @Autowired
    private TDsTaskInstanceService dsService;

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        TableMetaInfo metaInfo = param.getMetaInfo();
        String name = CacheUtil.getKey(metaInfo);
        String assessDate = param.getAssessDate();

        Integer days = getIntegerValue(param.getMetric(), "days");
        Integer percent = getIntegerValue(param.getMetric(), "n");
        LocalDate date = LocalDate.parse(assessDate).minusDays(days);
        // 查询本天到前3天调度成功的task

        List<TDsTaskInstance> successRecords = dsService.list(
                new QueryWrapper<TDsTaskInstance>()
                        .eq("name", name)
                        .ge("date(start_time)", date)
                        .eq("state", MetaConstant.TASK_STATE_SUCCESS)
                        .select("date(start_time) name", "timestampdiff(SECOND,start_time, end_time) id")
        );

        // 判断是不是只有一天的调度信息
        if (successRecords.size() <= 1){
            return;
        }
        // 取出今天的调度时间
        long timeToday = successRecords.stream()
                .filter(task -> assessDate.equals(task.getName()))
                .mapToLong(TDsTaskInstance::getId)
                .sum();

        // 取前days的平均值
        double timeAvg = successRecords.stream()
                .filter(task -> !assessDate.equals(task.getName()))
                .mapToLong(TDsTaskInstance::getId)
                .average()
                .getAsDouble();
        // 计算阈值
        BigDecimal limit = BigDecimal.valueOf(timeAvg).multiply(BigDecimal.valueOf(percent + 100)).movePointLeft(2);

        // 判断
        if (BigDecimal.valueOf(timeToday).compareTo(limit) >0 ){
            String comment = "产出耗时%d秒, 超过了前%d天平均时间%f的的%d%%";
            setScore(assessDetail,BigDecimal.ZERO, "表产出时效异常",
                    String.format(comment, timeToday, days, timeAvg,percent),
                    false, null);
        }
    }
}
