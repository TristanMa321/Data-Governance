package com.atguigu.dga.score.mapper;

import com.atguigu.dga.score.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 表治理考评情况 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
@Mapper
public interface GovernanceAssessTableMapper extends BaseMapper<GovernanceAssessTable> {
    @Select(" select" +
            "    null as id," +
            "    #{dt} as assess_date," +
            "    table_name," +
            "    schema_name," +
            "    tec_owner," +
            "    avg(if(governance_type = 'SPEC', assess_score, null)) score_spec_avg," +
            "    avg(if(governance_type = 'STORAGE', assess_score, null)) score_storage_avg," +
            "    avg(if(governance_type = 'CALC', assess_score, null)) score_calc_avg," +
            "    avg(if(governance_type = 'QUALITY', assess_score, null)) score_quality_avg," +
            "    avg(if(governance_type = 'SECURITY', assess_score, null)) score_security_avg," +
            "    null score_on_type_weight," +
            "    count(if(assess_score < 10, id, null)) problem_num," +
            "    now() as create_time" +
            " from governance_assess_detail" +
            " where assess_date = #{dt} and schema_name = #{db}" +
            " group by schema_name, table_name, tec_owner")
    List<GovernanceAssessTable> calScore(@Param("db") String db, @Param("dt") String assessDate);
}
