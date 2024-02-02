package com.atguigu.dga.assess.assessor.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.Field;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.CacheUtil;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.atguigu.dga.config.CacheUtil.getKey;

/**
 同层的两个表字段重复(字段名+注释)超过{percent}%，则给0分，其余给10分
 */
@Component("TABLE_SIMILAR")
public class CheckSimiliarTable extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        // 获取当前表的层级
        TableMetaInfo metaInfo = param.getMetaInfo();
        TableMetaInfoExtra metaInfoExtra = metaInfo.getTableMetaInfoExtra();
        String dwLevel = metaInfoExtra.getDwLevel();
        // 获取当前表的字段名+注释的set
        Set<String> currentTableFields = getFieldAndComment(metaInfo);
        // 获取matric表给的阈值
        Integer limitPercent = getIntegerValue(param.getMetric(), "percent");
        // 获取相同层次表的原数据信息
        Map<String, TableMetaInfo> metaInfoMap = CacheUtil.metaInfoMap;
        List<TableMetaInfo> compareTableMetaInfo = metaInfoMap.entrySet().stream()
                .filter(entry -> dwLevel.equals(entry.getValue().getTableMetaInfoExtra().getDwLevel())
                        &&
                        !getKey(metaInfo).equals(getKey(entry.getValue())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        // 创建集合收集相似的表
        List<JSONObject> result = new ArrayList<>();
        // 逐个对比
        for (TableMetaInfo compareTableInfo : compareTableMetaInfo) {
            Set<String> compareFields = getFieldAndComment(compareTableInfo);
            // 看相似 取交集
            compareFields.retainAll(currentTableFields);
            if (!compareFields.isEmpty()){
                // 计算比例看是否超过阈值
                BigDecimal percent = BigDecimal.valueOf(compareFields.size())
                        .divide(BigDecimal.valueOf(currentTableFields.size()), 2, RoundingMode.HALF_UP)
                        .movePointRight(2);

                if (percent.compareTo(BigDecimal.valueOf(limitPercent)) >= 0){
                    // 超过阈值的信息 都封装好
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("table", getKey(metaInfo));
                    jsonObject.put("similarTable", getKey(compareTableInfo));
                    jsonObject.put("percent",percent);
                    jsonObject.put("fields", compareFields);
                    result.add(jsonObject);

                }

            }
        }
        if (!result.isEmpty()){
            setScore(assessDetail,BigDecimal.ZERO, "存在相似表", JSON.toJSONString(result), false, null);
        }
    }
    // 获取一张表所有的字段名(列名)+注释的set
    private Set<String> getFieldAndComment(TableMetaInfo metaInfo){
        String colNameJson = metaInfo.getColNameJson();
        List<Field> fields = JSON.parseArray(colNameJson, Field.class);
        return fields.stream()
                .map(f ->  f.getName() + "_" + (f.getComment() == null ? "" : f.getComment()))
                .collect(Collectors.toSet());
    }
}
