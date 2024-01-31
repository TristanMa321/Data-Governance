package com.atguigu.dga;

import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.google.common.collect.Sets;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;

/**
 * @date 2024-01-30
 */
@SpringBootTest
public class AssessTest {
    @Autowired
    private TableMetaInfoService metaInfoService;
    @Test
    public void testQueryMetaInfo(){
        System.out.println(metaInfoService.queryMetaInfo("gmall", "2024-01-29"));
    }

    @Autowired
    private GovernanceAssessDetailService detailService;
    @Test
    public void testAssess(){
        detailService.generateAssessDetail("gmall","2024-01-29");
    }

    @Test
    public void testBigDecimal(){
        // 构造
    }

        /*
        set集合的交并差运算。
            set1:  [1,2,3]
            set2:   [1,4]

            交集： [1]
            并集： [1,2,3,4]
            set1差集set2: [2,3]
            set2差集set1" [4]

         工具类：
               apache.common.lang3
               guava(google java)
               HuTool(国内)
     */
    @Test
    public void testSet(){
        // 使用apache.common.lang3 Sets 快速创建一个集合
        HashSet<Integer> set1 = Sets.newHashSet(1, 2, 3);
        HashSet<Integer> set2 = Sets.newHashSet(1, 4);
        // 交集
        set1.retainAll(set2);
        // 并集
        set1.addAll(set2);
        // 差集
        set1.removeAll(set2);
    }
    @Autowired
    private ApplicationContext context;

    @Test
    public void testPermission() throws IOException {
        FileSystem hdfs = context.getBean(FileSystem.class);
        Path path = new Path("/warehouse/gmall/dws/dws_interaction_sku_favor_add_1d");
        FileStatus fileStatus = hdfs.getFileStatus(path);
        System.out.println(fileStatus.getPermission());     // rwxr-xr-x
    }

}
