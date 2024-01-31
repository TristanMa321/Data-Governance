package com.atguigu.dga.meta.mapper;

import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 元数据表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-01-28
 */
@Mapper
public interface TableMetaInfoMapper extends BaseMapper<TableMetaInfo> {
    /*
        1.可以使用MybatisPlus,
             第一步查询table_meta_info_extra，查到符合条件的数据
             第二步查询table_meta_info,遍历第一步查询的数据集合，为集合中的每一个元素，到table_meta_info查询，之后封装
                    发多少次请求:    1 * 79
             MybatisPlus不擅长做join操作
                省去写sql，麻烦在于多写java代码

        2.不使用MybatisPlus，自己编写joinsql
                   发1次请求。
                    自己写sql，麻烦在于写sql，java代码简单。

     */


    List<PageTableMetaInfo> queryMetaInfoList(@Param("start") Integer start,
                                              @Param("size") Integer pageSize,
                                              @Param("tname") String tableName,
                                              @Param("db") String schemaName,
                                              @Param("level") String dwLevel);

    Integer queryMetaInfoTotal(@Param("tname") String tableName,
                               @Param("db") String schemaName,
                               @Param("level") String dwLevel);
    // 查询要考评的当天原数据信息
    List<TableMetaInfo> queryMetaInfo(@Param("db") String db, @Param("dt") String assessDate);
}
