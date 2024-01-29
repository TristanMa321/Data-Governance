package com.atguigu.dga;

import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

/**
 * @date 2024-01-28
 */
@SpringBootTest
public class MetaTest {
    @Autowired
    private ApplicationContext context;
    @Test
    public void testHiveClient() throws TException {
        HiveMetaStoreClient client = context.getBean(HiveMetaStoreClient.class);
        // 操作
        // 获取库下所有的表
        System.out.println(client.getAllTables("gmall"));
        // 获取某张表的原数据信息
        System.out.println(client.getTable("gmall", "ods_spu_info_full"));

        client.close();

    }


    @Autowired
    private TableMetaInfoService metaInfoService;
    @Test
    public void testInitMetaInfo() throws TException, IOException {
        metaInfoService.initMetaInfo("gmall","2024-01-27");

    }
    @Autowired
    private TableMetaInfoExtraService extraService;
    @Test
    public void testExtraInfo() throws TException, IOException {
        extraService.initMetaInfoExtra("gmall");

    }

    @Test
    public void testQueryMetaInfoList() throws TException, IOException {
        System.out.println(metaInfoService.queryMetaInfoList(
                1, 10, "log", null, ""));

        System.out.println(metaInfoService.queryMetaInfoTotal(
                null, null, "ODS"));


    }
}
