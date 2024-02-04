package com.atguigu.dga.assess.mapper;

import com.atguigu.dga.assess.bean.TDsTaskInstance;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-02-02
 */
@Mapper
@DS("ds")
public interface TDsTaskInstanceMapper extends BaseMapper<TDsTaskInstance> {

}
