package com.atguigu.dga.assess.service.impl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.assess.mapper.GovernanceAssessDetailMapper;
import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import com.atguigu.dga.assess.service.GovernanceMetricService;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 治理考评结果明细 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-30
 */
@Service
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {
    /*
        4.把考评结果（GovernanceAssessDetail）写入数据库
     */
    @Override
    public void generateAssessDetail(String db, String assessDate) {
        // 为保证幂等性，删除存在的数据
        remove(new QueryWrapper<GovernanceAssessDetail>().eq("schema_name",db).eq("assess_date",assessDate));
        // 抽取要考评的数据（原数据表+辅助信息表）
        List<TableMetaInfo> metaInfo =queryMetaInfo(db,assessDate);
        // 查询考评指标
        List<GovernanceMetric> metrics = metricService.list(new QueryWrapper<GovernanceMetric>().eq("is_disabled", "否"));
        // 考评
        List<GovernanceAssessDetail> details = assess(metaInfo,metrics,assessDate);
        // 结果写入数据库
        saveBatch(details);
    }
    @Autowired
    ApplicationContext context;
    /*
       考评的核心流程
            为每个模板类AssessorTemplate的子类 创建唯一的name，name和GovernanceMetric表的metric_code列的值一致
     */
    private List<GovernanceAssessDetail> assess(List<TableMetaInfo> metaInfo, List<GovernanceMetric> metrics, String assessDate) {
        List<GovernanceAssessDetail> details = new ArrayList<>();
        for (TableMetaInfo tableMetaInfo : metaInfo) {
            for (GovernanceMetric metric : metrics) {
                // 去编写一个考评指标的业务类
                AssessorTemplate t =  context.getBean(metric.getMetricCode() ,AssessorTemplate.class);
                GovernanceAssessDetail assessDetail = t.assess(new AssessParam(assessDate, tableMetaInfo, metric));
                details.add(assessDetail);
            }
        }
        return details;
    }

    // 用join查询两个表table_meta_info table_meta_info_extra
    /*
        正常应该使用baseMapper，但是这个dao是处理这个governance_assess_detail表的，
        所以我们要重新引入之前操作table_meta_info表的mapper, 但需要通过service来创建里面的方法
     */
    @Autowired
    private TableMetaInfoService metaInfoService;
    private List<TableMetaInfo> queryMetaInfo(String db, String assessDate) {

        return metaInfoService.queryMetaInfo(db,assessDate);
    }

    @Autowired
    private GovernanceMetricService metricService;
}
