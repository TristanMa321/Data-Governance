package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.hadoop.hive.metastore.api.MetaException;

/**
 * <p>
 * 元数据表附加信息 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-29
 */
public interface TableMetaInfoExtraService extends IService<TableMetaInfoExtra> {
    // 生成所有表的辅助信息
    void initMetaInfoExtra(String db) throws MetaException;
}
