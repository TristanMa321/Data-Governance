package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.hadoop.hive.metastore.api.MetaException;

import java.io.IOException;

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
}
