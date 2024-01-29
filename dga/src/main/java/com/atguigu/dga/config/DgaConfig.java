package com.atguigu.dga.config;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * @date 2024-01-28
 */
/*
   知识点：所有客户端都应该随用随建，用完就关
 */
@Configuration
public class DgaConfig {
    @Value("${hive.client.uri}")
    private String hiveUri;
    @Bean
    @Scope("prototype")
    public HiveMetaStoreClient createHiveMetaStoreClient(){
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        // 客户端连接服务端，需要配置地址和端口
        conf.set("hive.metastore.uris",hiveUri);
        try {
            HiveMetaStoreClient client = new HiveMetaStoreClient(conf);
            return client;
        } catch (MetaException e) {
            throw new RuntimeException(e);
        }

    }
    @Value("${hdfs.uri}")
    private String hdfsUri;
    @Value("${hdfs.admin.user}")
    private String hdfsAdmin;
    @Bean
    @Scope("prototype")
    public FileSystem createHDFSClient(){
        try {
            FileSystem hdfsClient = FileSystem.get(new URI(hdfsUri), new org.apache.hadoop.conf.Configuration(), hdfsAdmin);
            return hdfsClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
