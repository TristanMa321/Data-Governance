package com.atguigu.dga.config;

import com.atguigu.dga.meta.bean.TableMetaInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存工具
 *
 * @date 2024-02-01
 */
public class CacheUtil {
    /*
        缓存所有表的原数据信息
            k: 库名.表名
            v: TableMetaInfo
     */
    public static Map<String, TableMetaInfo> metaInfoMap = new HashMap<>();

    // 返回库名.表名
    public static String getKey(TableMetaInfo metaInfo){
        return metaInfo.getSchemaName() + "." + metaInfo.getTableName();
    }
}
