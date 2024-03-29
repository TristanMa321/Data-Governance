package com.atguigu.dga.score.mapper;

import com.atguigu.dga.score.bean.GovernanceAssessTecOwner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 技术负责人治理考评表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-02-04
 */
@Mapper
public interface GovernanceAssessTecOwnerMapper extends BaseMapper<GovernanceAssessTecOwner> {
    @Select(" select null as id," +
            "       #{dt} as assess_date," +
            "       tec_owner," +
            "       avg(score_spec_avg) score_spec," +
            "       avg(score_storage_avg) score_storage," +
            "       avg(score_calc_avg) score_calc," +
            "       avg(score_quality_avg) score_quality," +
            "       avg(score_security_avg) score_security," +
            "       avg(score_on_type_weight) score," +
            "       count(if(problem_num>0, id, null))table_num," +
            "       sum(problem_num) as problem_num," +
            "       now() as create_time" +
            " from governance_assess_table" +
            " where assess_date = #{dt}" +
            " group by tec_owner")
    List<GovernanceAssessTecOwner> calScore(@Param("dt") String assessDate);
}
