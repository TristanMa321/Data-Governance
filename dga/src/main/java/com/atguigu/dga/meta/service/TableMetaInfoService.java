package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.hadoop.hive.metastore.api.MetaException;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 元数据表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-28
 */
public interface TableMetaInfoService extends IService<TableMetaInfo> {
    // 手动更新全库原数据
    void initMetaInfo(String db, String accessDate) throws MetaException, IOException;

    List<PageTableMetaInfo> queryMetaInfoList(int start, Integer pageSize, String tableName, String schemaName, String dwLevel);

    Integer queryMetaInfoTotal(String tableName, String schemaName, String dwLevel);

    //查询要考评的原数据信息
    List<TableMetaInfo> queryMetaInfo(String db, String assessDate);
}
