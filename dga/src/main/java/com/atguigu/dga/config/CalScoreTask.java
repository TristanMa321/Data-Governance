package com.atguigu.dga.config;

import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.atguigu.dga.score.service.CalScoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

/**
 * @date 2024-02-05
 */
@Component
public class CalScoreTask {
    // 获取配置信息中，所有要考评的库
    @Value("${dbs}")
    private String dbs;

    @Autowired
    private TableMetaInfoService metaInfoService;
    @Autowired
    private TableMetaInfoExtraService extraService;
    @Autowired
    private GovernanceAssessDetailService detailService;
    @Autowired
    private CalScoreService calScoreService;

    //数仓调度完成后，在进行考评
    @Scheduled(cron = "0 0 8 * * *")
    public void scheduleAssess() throws IOException, MetaException, ExecutionException, InterruptedException {
        governanceAssess(null);
    }
    /*
        根据指定考评日期，对所有的库进行重新评估
        流程：
            1.采集原数据
            2.重新考评
            3.重新算分
     */
    public void governanceAssess(String assessDate) throws IOException, MetaException, ExecutionException, InterruptedException {
        if (StringUtils.isBlank(assessDate)){
            // 没有传入日期， 取当天
            assessDate = LocalDate.now().toString();
        }

        String[] dbNames = dbs.split(",");
        for (String db : dbNames) {
            // 1.采集原数据
            metaInfoService.initMetaInfo(db, assessDate);
            extraService.initMetaInfoExtra(db);
            // 2.重新考评
            detailService.generateAssessDetail(db,assessDate);
            // 3.重新算分
            calScoreService.calScore(db, assessDate);
        }
    }
}
