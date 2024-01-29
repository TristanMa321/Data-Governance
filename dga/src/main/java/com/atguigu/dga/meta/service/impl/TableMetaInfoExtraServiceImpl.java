package com.atguigu.dga.meta.service.impl;

import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.mapper.TableMetaInfoExtraMapper;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 元数据表附加信息 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-29
 */
@Service
public class TableMetaInfoExtraServiceImpl extends ServiceImpl<TableMetaInfoExtraMapper, TableMetaInfoExtra> implements TableMetaInfoExtraService {

    /*
        这个业务系统，只是做插入信息用
        辅助信息不经常变动, 只有创建新表时，才需要写入信息，如果一张表有了辅助信息，无需写入
     */
    @Autowired
    private ApplicationContext context;
    @Override
    public void initMetaInfoExtra(String db) throws MetaException {
        // 查询新表(数仓中因为业务需求，新建的表)
        // 步骤一：先查询table_meta_info_extra中当前已经有的表
        Set<String> existsTableNames = list(new QueryWrapper<TableMetaInfoExtra>().eq("schema_name", db))
                .stream()
                .map(info -> info.getTableName())
                .collect(Collectors.toSet());
        // 步骤二：查询对应schema_name下所有的表，根据老表，过滤得到新表
        HiveMetaStoreClient client = context.getBean(HiveMetaStoreClient.class);
        List<String> allTables = client.getAllTables(db);
        List<String> newTables = allTables.stream()
                .filter(name -> !existsTableNames.contains(name))
                .collect(Collectors.toList());
        // 为新表生成辅助信息，存入到数据库中
        List<TableMetaInfoExtra> infos = newTables.stream()
                .map(name -> {
                    TableMetaInfoExtra extra = new TableMetaInfoExtra();
                    extra.setSchemaName(db);
                    extra.setTableName(name);
                    // 其他信息应该由员工手动录入
                    initExtraInfo(extra);
                    extra.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    return extra;
                })
                .collect(Collectors.toList());
        // 写入到数据库
        saveBatch(infos);
    }

    private void initExtraInfo(TableMetaInfoExtra extra) {
        String [] busiOwner = {"张三","李四","王五","赵六"};
        String [] techOwner = {"张小三","李中四","王大五","赵老六"};

        extra.setBusiOwnerUserName(busiOwner[RandomUtils.nextInt(0, busiOwner.length)]);
        extra.setTecOwnerUserName(techOwner[RandomUtils.nextInt(0, techOwner.length)]);
        extra.setLifecycleType(MetaConstant.LIFECYCLE_TYPE_UNSET);
        extra.setLifecycleDays(-1l);
        extra.setSecurityLevel(MetaConstant.SECURITY_LEVEL_UNSET);
        extra.setDwLevel(extra.getTableName().substring(0,3).toUpperCase());
    }
}
