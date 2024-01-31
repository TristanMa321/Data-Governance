package com.atguigu.dga.assess.assessor.spec;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.Field;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**

   评分标准: 有备注字段/所有字段 *10分

 */
@Component("HAVE_FIELD_COMMENT")
public class CheckFieldMissComment extends AssessorTemplate
{

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        TableMetaInfo metaInfo = param.getMetaInfo();
        // 获取表中的所有字段（json格式）
        String colNameJson = metaInfo.getColNameJson();
        // 转成bean的集合
        List<Field> fields = JSON.parseArray(colNameJson, Field.class);
        // 获取有备注的字段
        List<String> noCommentFields = fields.stream()
                .filter(f -> !f.getSetComment()).map(Field::getName).collect(Collectors.toList());

        // 打分, 默认是满分，对有问题的进行改分
        if (!noCommentFields.isEmpty()) {
            setScore(assessDetail,
                    BigDecimal.valueOf(fields.size() - noCommentFields.size())
                            .divide(BigDecimal.valueOf(fields.size()), 2, RoundingMode.HALF_UP)
                            .movePointRight(1),
                    "部分字段没有注释", JSON.toJSONString(noCommentFields), false, null);
        }
    }
}
