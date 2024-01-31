package com.atguigu.dga.meta.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.mapper.TableMetaInfoMapper;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 元数据表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-28
 */

/*
    技术点：拿到hive表中原数据
            1.借助JDBC，发送 desc formatted 表名 到hiveserver2获取表的元数据信息。
                 弊端：  hiveserver2不擅长高并发的访问。
                        本身效率也很低

            2.Hive考虑到第三方程序访问元数据这个场景，专门开发了一个服务Hive MetaStore Service,
                专门负责为第三方程序(java,spark,flink)提供hive中的元数据信息。
                专业，高效。
                    先启动服务:  hive --service metastore

 */
@Service
public class TableMetaInfoServiceImpl extends ServiceImpl<TableMetaInfoMapper, TableMetaInfo> implements TableMetaInfoService {


    @Override
    public void initMetaInfo(String db, String assessDate) throws MetaException, IOException {
        //1. 保证原数据信息不回重复采集：先删除之前的
        remove(new QueryWrapper<TableMetaInfo>().eq("schema_name",db).eq("assess_date",assessDate));
        //2. 从hive中采集原数据
        List<TableMetaInfo> tableMetaInfos = extractMetaInfoFromHive(db, assessDate);

        //3. 从hdfs中采集原数据
        List<TableMetaInfo> metaInfos = extractMetaInfoFromHDFS(tableMetaInfos);
        //4. 写入目标表
        saveBatch(metaInfos);
    }

    @Override
    public List<PageTableMetaInfo> queryMetaInfoList(int start, Integer pageSize, String tableName, String schemaName, String dwLevel) {
        return baseMapper.queryMetaInfoList(start, pageSize, tableName, schemaName, dwLevel);
    }

    @Override
    public Integer queryMetaInfoTotal(String tableName, String schemaName, String dwLevel) {
        return baseMapper.queryMetaInfoTotal(tableName, schemaName, dwLevel);
    }

    @Override
    public List<TableMetaInfo> queryMetaInfo(String db, String assessDate) {
        return baseMapper.queryMetaInfo(db, assessDate);
    }

    private List<TableMetaInfo> extractMetaInfoFromHDFS(List<TableMetaInfo> tableMetaInfos) throws IOException {
        // 获取hdfs客户端
        FileSystem hdfsClient = context.getBean(FileSystem.class);

        // 遍历每个tableMetaInfos，为每个tableMetaInfos补充信息
        FsStatus status = hdfsClient.getStatus();
        long capacity = status.getCapacity();
        long remaining = status.getRemaining();
        long used = status.getUsed();
        for (TableMetaInfo tableMetaInfo : tableMetaInfos) {
            tableMetaInfo.setFsCapcitySize(capacity);
            tableMetaInfo.setFsRemainSize(remaining);
            tableMetaInfo.setFsUsedSize(used);
            /*
                table_size,table_total_size,table_last_modify_time,
                table_last_access_time都需要进行聚合运算。
             */
            Path path = new Path(tableMetaInfo.getTableFsPath());
            statsSizeAndTime(path, hdfsClient, tableMetaInfo);
        }

        hdfsClient.close();
        return tableMetaInfos;
    }

    private void statsSizeAndTime(Path path, FileSystem hdfsClient, TableMetaInfo tableMetaInfo) throws IOException {
        // 获取表所在的hdfs的目录下的所有子内容
        FileStatus[] fileStatuses = hdfsClient.listStatus(path);

        for (FileStatus fileStatus : fileStatuses) {
            // 判断当前fileStatus是目录还是文件，如果是文件，获取大小，累加
            if (fileStatus.isFile()){
                tableMetaInfo.setTableSize(
                        tableMetaInfo.getTableSize() + fileStatus.getLen()
                );
                tableMetaInfo.setTableTotalSize(
                        tableMetaInfo.getTableSize()+fileStatus.getLen()*fileStatus.getReplication()
                );
                tableMetaInfo.setTableLastAccessTime(
                        new Timestamp(Math.max(tableMetaInfo.getTableLastAccessTime().getTime(),fileStatus.getAccessTime()))
                );
                tableMetaInfo.setTableLastModifyTime(
                        new Timestamp(Math.max(tableMetaInfo.getTableLastModifyTime().getTime(),fileStatus.getModificationTime()))
                );
            }else {
                // 不是文件，需要递归继续探测目录中的所有子内容
                statsSizeAndTime(fileStatus.getPath(), hdfsClient, tableMetaInfo);
            }
        }
    }

    @Autowired
    private ApplicationContext context;
    private List<TableMetaInfo> extractMetaInfoFromHive(String db, String assessDate) throws MetaException {
        // 1.获取HiveMetaStoreClient
        HiveMetaStoreClient client = context.getBean(HiveMetaStoreClient.class);
        // 先查所有表
        List<String> allTables = client.getAllTables("gmall");
        // 遍历每张表，获取原数据信息
        List<TableMetaInfo> metaInfo = allTables.stream()
                .map(name -> {
                    TableMetaInfo tableMetaInfo = new TableMetaInfo();
                    try {
                        // 根据表名查询原数据
                        Table table = client.getTable(db, name);
                        StorageDescriptor sd = table.getSd();
                        // 设置原数据信息
                        tableMetaInfo.setTableName(table.getTableName());
                        tableMetaInfo.setSchemaName(table.getDbName());
                        tableMetaInfo.setColNameJson(JSON.toJSONString(sd.getCols()));
                        tableMetaInfo.setPartitionColNameJson(JSON.toJSONString(table.getPartitionKeys()));
                        tableMetaInfo.setTableFsOwner(table.getOwner());
                        tableMetaInfo.setTableParametersJson(JSON.toJSONString(table.getParameters()));
                        tableMetaInfo.setTableComment(table.getParameters().get("comment"));
                        tableMetaInfo.setTableFsPath(sd.getLocation());
                        tableMetaInfo.setTableInputFormat(sd.getInputFormat());
                        tableMetaInfo.setTableOutputFormat(sd.getOutputFormat());
                        tableMetaInfo.setTableRowFormatSerde(sd.getSerdeInfo().getSerializationLib());
                        tableMetaInfo.setTableCreateTime(new Timestamp(table.getCreateTime() * 1000l));
                        tableMetaInfo.setTableType(table.getTableType());
                        tableMetaInfo.setTableBucketColsJson(JSON.toJSONString(sd.getBucketCols()));
                        tableMetaInfo.setTableBucketNum(sd.getNumBuckets() + 0l);
                        tableMetaInfo.setTableSortColsJson(JSON.toJSONString(sd.getSortCols()));
                        tableMetaInfo.setAssessDate(assessDate);
                        // 原数据产生的时间
                        tableMetaInfo.setCreateTime(new Timestamp((System.currentTimeMillis())));
                        return tableMetaInfo;
                    } catch (TException e) {
                        throw new RuntimeException(e);
                    }

                })
                .collect(Collectors.toList());
        client.close();
        return metaInfo;
    }
}
