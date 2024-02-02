package com.atguigu.dga.assess.assessor.quality;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 必须日分区表
 前一天数据产出的数据量，超过前days天平均产出量{upper_limit}% ，或低于{lower_limit}%  ，则给0分，其余10分
 */
@Component("TABLE_PRODUCT_VOLUME_MONITOR")
public class CheckTableProduceVolume extends AssessorTemplate
{
    /*
         考评日期 和 数仓中最近一次统计的数据的日期的关系。
            考评日期 = 调度日期 =  最近一次统计的数据的日期 + 1
            assessDate       数据
            2024-01-31       2024-01-30
            2024-01-30       2024-01-29
            2024-01-29       2024-01-28
            2024-01-28       2024-01-27

            days = 3。
                统计 dt=2024-01-27 --- dt=2024-01-29 的平均产生数据量
                用  dt=2024-01-30数据产出量 和平均值进行对比，看是否超过阈值。
     */
    @Autowired
    private ApplicationContext context;

    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws IOException {
        // 取参数
        TableMetaInfo metaInfo = param.getMetaInfo();
        GovernanceMetric metric = param.getMetric();
        Integer days = getIntegerValue(metric, "days");
        Integer ul = getIntegerValue(metric, "upper_limit");
        Integer ll = getIntegerValue(metric, "lower_limit");
        // 必须是日分区表
        String lifecycleType = metaInfo.getTableMetaInfoExtra().getLifecycleType();
        if (!MetaConstant.LIFECYCLE_TYPE_DAY.equals(lifecycleType)){
            return;
        }
        // 获取考评日期
        // String assessDate = param.getAssessDate();
        // 为了测试，和我们数仓相对应，我们将考评日期写死
        String assessDate = "2022-06-11";
        String lastEventDate = LocalDate.parse(assessDate).minusDays(1).toString();
        // 获取hdfs客户端
        FileSystem hdfs = context.getBean(FileSystem.class);
        // 获取表的路径
        String tableFsPath = metaInfo.getTableFsPath();
        // 准备一个集合来收集 统计的相关分区的数据量
        List<PartitionDataSize> status = statusTablePartitionSize(lastEventDate, days, hdfs, tableFsPath);
        // 比较样本不足，只有今天一天的，无需对比产出量波动
        if (status.size() <= 1){
            return;
        }
        // 先获取最近一次统计的数据的日期分区的数据量
        long lastDatePartitionSize = status.stream()
                .filter(p -> lastEventDate.equals(p.getDt()))
                .mapToLong(p -> p.getSize())
                .sum();
        // 统计前days天的平均产出
        double avgSize = status.stream()
                .filter(p -> !lastEventDate.equals(p.getDt()))
                .mapToDouble(p -> p.getSize())
                .average()
                .getAsDouble();
        // 计算阈值
        BigDecimal uplimit = BigDecimal.valueOf(avgSize).multiply(BigDecimal.valueOf(ul + 100)).movePointLeft(2);
        BigDecimal lowerlimit = BigDecimal.valueOf(avgSize).multiply(BigDecimal.valueOf(100 - ll)).movePointLeft(2);

        // 打分
        BigDecimal ldps = BigDecimal.valueOf(lastDatePartitionSize);
        if (ldps.compareTo(uplimit) >0 || ldps.compareTo(lowerlimit)<0){
            // 超出范围 0分
            String comment = "当前考评日期是：%s,最近导入的数据的日期是：%s, 导入的数据的产出量是：%d, " +
                    "超过了最近%d天的平均产出量%f的%d%%, "+"或低于%d%%下限";
            setScore(
                    assessDetail,
                    BigDecimal.ZERO,
                    "产出量异常",
                    String.format(comment, assessDate, lastEventDate,lastDatePartitionSize,days,avgSize,ul,ll),
                    false,
                    null
            );
        }


        hdfs.close();
    }

    private List<PartitionDataSize> statusTablePartitionSize(String lastEventDate, Integer days, FileSystem hdfs, String tableFsPath) throws IOException {
        ArrayList<PartitionDataSize> result = new ArrayList<>();
        for (int i = 0; i <= days; i++) {
            // 获取分区目录   格式：/表目录/dt=分区日期
            String dt = LocalDate.parse(lastEventDate).minusDays(i).toString();
            Path dtPath = new Path(tableFsPath, "dt=" + dt);
            // 判断路径是否存在
            if (hdfs.exists(dtPath)){
                // 先付个默认值
                PartitionDataSize partitionDataSize = new PartitionDataSize(dt, 0l);
                // 递归遍历分区目录中所有的数据量
                status(partitionDataSize,hdfs,dtPath);
                result.add(partitionDataSize);
            }
        }
        return result;
    }
    // 遍历一个dt目录下所有数据量
    private void status(PartitionDataSize partitionDataSize, FileSystem hdfs, Path dtPath) throws IOException {
        FileStatus[] fileStatuses = hdfs.listStatus(dtPath);
        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isFile()){
                partitionDataSize.setSize(partitionDataSize.getSize() + fileStatus.getLen());
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class PartitionDataSize{
        private String dt;
        private Long size;
    }

}
