package com.atguigu.dga.assess.assessor.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 检查该表最高权限的目录或者文件，如果超过文件超过{file_permission}或者目录超过{dir_permission}则给0分 其余给10分
 核心逻辑：去找哪些文件超过了阈值
 */
@Component("PERMISSION_CHECK")
public class CheckFilePermission extends AssessorTemplate
{
    @Autowired
    private ApplicationContext context;

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws IOException {
        GovernanceMetric metric = param.getMetric();
        TableMetaInfo metaInfo = param.getMetaInfo();
        // 在metric表获取阈值
        Integer filePermission = getIntegerValue(metric, "file_permission");
        Integer dirPermission = getIntegerValue(metric, "dir_permission");
        // 获取hdfs的客户端
        FileSystem hdfs = context.getBean(FileSystem.class);
        // 准备集合，收集超过阈值的文件
        List<JSONObject> result = new ArrayList<>();
        // 拿到表对应的file status(但可能表包含表)
        FileStatus fileStatus = hdfs.getFileStatus(new Path(metaInfo.getTableFsPath()));
        // 判断当前表目录及其子文件是否超过阈值
        checkPermission(result, hdfs, fileStatus, filePermission, dirPermission);
        // 判断结果
        if (!result.isEmpty()){
            setScore(assessDetail, BigDecimal.ZERO, "表目录中有文件或目录超过权限阈值", JSON.toJSONString(result),false, null);
        }

        hdfs.close();
    }

    private void checkPermission(List<JSONObject> result, FileSystem hdfs, FileStatus fileStatus, Integer filePermission, Integer dirPermission) throws IOException {
        Integer permission = getPermission(fileStatus);
        // 判断当前这个文件是目录还是文件
        if (fileStatus.isFile()){
            // 核心代码 检测 文件 是否超过阈值
            check(result, filePermission, permission, fileStatus);
        }else {
            // 是目录，检测当前目录是否超过阈值
            check(result, dirPermission, permission, fileStatus);
            // 并且 检测当前目录的子目录是否超过
            FileStatus[] subFileStatus = hdfs.listStatus(fileStatus.getPath()); // 列出路径下所有子文件的fileStatus
            for (FileStatus status : subFileStatus) {
                checkPermission(result,hdfs,status,filePermission,dirPermission);
            }

        }
    }

    private void check(List<JSONObject> result, Integer limit, Integer permission, FileStatus fileStatus) {
        if (permission > limit){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("path", fileStatus.getPath().getParent()+"/"+fileStatus.getPath().getName());
            jsonObject.put("permission", permission);
            jsonObject.put("limit", limit);
            result.add(jsonObject);
        }
    }

    // 获取路径下文件的权限数字
    private Integer getPermission(FileStatus fileStatus) throws IOException {
        FsPermission permission = fileStatus.getPermission();

        return permission.getUserAction().ordinal() * 100
                + permission.getGroupAction().ordinal() * 10
                + permission.getOtherAction().ordinal();
    }

}
