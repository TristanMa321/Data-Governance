package com.atguigu.dga.score.mapper;

import com.atguigu.dga.score.bean.GovernanceAssessGlobal;
import com.atguigu.dga.score.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 治理总考评表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
@Mapper
public interface GovernanceAssessGlobalMapper extends BaseMapper<GovernanceAssessGlobal> {
    @Select(" select null as id," +
            "       #{dt} as assess_date," +
            "       avg(score_spec_avg) * 10 score_spec," +
            "       avg(score_storage_avg) * 10 score_storage," +
            "       avg(score_calc_avg) * 10 score_calc," +
            "       avg(score_quality_avg) * 10 score_quality," +
            "       avg(score_security_avg) * 10 score_security," +
            "       avg(score_on_type_weight) score," +
            "       count(if(problem_num>0, id, null))table_num," +
            "       sum(problem_num) as problem_num," +
            "       now() as create_time" +
            " from governance_assess_table" +
            " where assess_date = #{dt}")
    GovernanceAssessGlobal calGlobalScore(@Param("dt") String assessDate);
}
