package com.atguigu.dga.assess.assessor.security;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.file.FileSystem;

/**
 检查该表最高权限的目录或者文件，如果超过文件超过{file_permission}或者目录超过{dir_permission}则给0分 其余给10分
 */
@Component("PERMISSION_CHECK")
public class CheckFilePermission extends AssessorTemplate
{
    @Autowired
    private ApplicationContext context;

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws IOException {
        GovernanceMetric metric = param.getMetric();
        // 在metric表获取阈值
        Integer filePermission = getIntegerValue(metric, "file_permission");
        Integer dirPermission = getIntegerValue(metric, "dir_permission");
        // 获取hdfs的客户端
        FileSystem hdfs = context.getBean(FileSystem.class);


        hdfs.close();
    }
}
