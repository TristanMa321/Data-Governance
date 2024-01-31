package com.atguigu.dga.assess.assessor;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;

import java.awt.print.PrinterGraphics;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @date 2024-01-30
 * 模板设局模式
 */
public  abstract class AssessorTemplate {
    public GovernanceAssessDetail assess(AssessParam param){
        String assessDate = param.getAssessDate();
        TableMetaInfo metaInfo = param.getMetaInfo();
        GovernanceMetric metric = param.getMetric();
        TableMetaInfoExtra extraMetaInfo = metaInfo.getTableMetaInfoExtra();

        GovernanceAssessDetail detail = new GovernanceAssessDetail();
        detail.setAssessDate(metaInfo.getAssessDate());
        detail.setTableName(metaInfo.getTableName());
        detail.setSchemaName(metaInfo.getSchemaName());
        detail.setMetricId(metric.getMetricCode());
        detail.setMetricName(metric.getMetricName());
        detail.setGovernanceType(metric.getGovernanceType());
        detail.setTecOwner(extraMetaInfo.getTecOwnerUserName());
        detail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        // 分数默认值
        detail.setAssessScore(BigDecimal.TEN);
        // URL默认值
        detail.setGovernanceUrl(metric.getGovernanceUrl());

        // 抽象核心逻辑代码
        // 选择try catch的原因是希望 某个表在考评时出错了 能继续执行
        try {
            // score, problem, comment, url在这个方法中由子类具体赋值
            assessScore(param, detail);
            return detail;
        } catch (Exception e) {
            detail.setIsAssessException("1");
            // 获取异常的堆栈信息
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            detail.setAssessExceptionMsg(stringWriter.toString());
            // 但为了方便调试，选择抛出异常，可以看到直观看到错误
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected abstract void assessScore(AssessParam param, GovernanceAssessDetail detail) throws Exception;

    // 为指标考评完，打分的方法
    protected void setScore(GovernanceAssessDetail detail, BigDecimal score, String problem,
                            String comment, boolean ifReplaceUrl, Long id){

        detail.setAssessScore(score);
        detail.setAssessProblem(problem);
        detail.setAssessComment(comment);
        if (ifReplaceUrl){
            detail.setGovernanceUrl(detail.getGovernanceUrl().replace("{id}",id.toString()));
        }
    }

    // 提取metric表的metric_params_json列
    protected Integer getIntegerValue(GovernanceMetric metric, String paramName){
        return (Integer) JSON.parseObject(metric.getMetricParamsJson()).get(paramName);
    }
}
