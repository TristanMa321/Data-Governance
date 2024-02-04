package com.atguigu.dga.assess.assessor.cacl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.TDsTaskInstance;
import com.atguigu.dga.assess.mapper.TDsTaskInstanceMapper;
import com.atguigu.dga.assess.service.TDsTaskInstanceService;
import com.atguigu.dga.config.CacheUtil;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;


/**

 检查某张表在DS 当天调度中是否有报错。有则给0分，否则给10分。
 ---------------------
 离线数仓的调度:
    以层为粒度调度，一层是一个Task，所有层在一个Flow中。
        不能满足我们的要求。
 --------------------------
    考评的粒度以表为单位。
        以层为粒度，如果这个Task失败了，你如何知道是Task中哪张表执行sql时失败了？

    要满足考评的需求，必须以表为粒度进行调度。
        每张表的调度是一个Task，所有表组成一个Flow。
    在调度时，Task的命名必须符合 库名.表名 的规范。
 --------------------------------
    以前的调度无法满足现在的需要。
        方式一: 需要重新调度。
        方式二：导入我提供的已经按照表为粒度调度完的元数据信息

 ----------------------------------
     DS在使用时，定义的Flow，Task，调度的Task，它们的信息都存在Mysql中。
        t_ds_process_definition： 存储定义的Flow
        t_ds_process_instance： 存储flow的实例
        t_ds_task_definition： 存储Task的定义
        t_ds_task_instance(使用)： 存储Task的实例允许的情况。
 ---------------------------
 调度时，是一天一调度，在t_ds_task_instance，每张表都会每天生成一条调度信息。
    调度日期 = 考评日期
    一张表一天会被调度N次(失败重试机制)
 */


@Component("TASK_FAILED")
public class CheckTaskFailed extends AssessorTemplate
{
    @Autowired
    private TDsTaskInstanceService dsService;

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        TableMetaInfo metaInfo = param.getMetaInfo();
        String name = CacheUtil.getKey(metaInfo);
        String assessDate = param.getAssessDate();

        // 查询在调度日期出错的记录
        List<TDsTaskInstance> failedRecords = dsService.list(
                new QueryWrapper<TDsTaskInstance>()
                        .eq("name", name)
                        .eq("date(start_time)", assessDate)
                        .eq("state", MetaConstant.TASK_STATE_FAILED)
        );

        if (!failedRecords.isEmpty()){
            setScore(assessDetail, BigDecimal.ZERO, "表调度时有失败的记录","",false,null);
        }
    }
}
