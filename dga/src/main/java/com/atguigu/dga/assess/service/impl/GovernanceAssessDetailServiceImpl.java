package com.atguigu.dga.assess.service.impl;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.assess.mapper.GovernanceAssessDetailMapper;
import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import com.atguigu.dga.assess.service.GovernanceMetricService;
import com.atguigu.dga.config.CacheUtil;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>
 * 治理考评结果明细 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-30
 */
@Service
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {
    /*
        4.把考评结果（GovernanceAssessDetail）写入数据库
     */
    @Override
    public void generateAssessDetail(String db, String assessDate) throws ExecutionException, InterruptedException {
        // 为保证幂等性，删除存在的数据
        remove(new QueryWrapper<GovernanceAssessDetail>().eq("schema_name",db).eq("assess_date",assessDate));
        // 抽取要考评的数据（原数据表+辅助信息表）
        List<TableMetaInfo> metaInfo =queryMetaInfo(db,assessDate);
        // 将上述查到的元数据，封装到一个map集合中方便后续某些指标，根据表名获取相关表的原数据信息
        saveMetaInfoToMap(metaInfo);
        // 查询考评指标
        List<GovernanceMetric> metrics = metricService.list(new QueryWrapper<GovernanceMetric>().eq("is_disabled", "否"));
        // 考评
        List<GovernanceAssessDetail> details = parallelAssess(metaInfo,metrics,assessDate);
        // 结果写入数据库
        saveBatch(details);
    }

    private void saveMetaInfoToMap(List<TableMetaInfo> metaInfo) {
        for (TableMetaInfo tableMetaInfo : metaInfo){
            CacheUtil.metaInfoMap.put(CacheUtil.getKey(tableMetaInfo), tableMetaInfo);
        }
    }

    @Autowired
    ApplicationContext context;
    /*
       考评的核心流程
            为每个模板类AssessorTemplate的子类 创建唯一的name，name和GovernanceMetric表的metric_code列的值一致
     */

    @Autowired
    private ThreadPoolTaskExecutor pool;
    private List<GovernanceAssessDetail> assess(List<TableMetaInfo> metaInfo, List<GovernanceMetric> metrics, String assessDate) {
        List<GovernanceAssessDetail> details = new ArrayList<>();
        for (TableMetaInfo tableMetaInfo : metaInfo) {
            for (GovernanceMetric metric : metrics) {
                // 去编写一个考评指标的业务类
                AssessorTemplate t =  context.getBean(metric.getMetricCode() ,AssessorTemplate.class);
                GovernanceAssessDetail assessDetail = t.assess(new AssessParam(assessDate, tableMetaInfo, metric));
                details.add(assessDetail);
            }
        }
        return details;
    }
    /*
        gpt解释并行流
        这段代码实现了一个并行化的数据治理评估过程，其中涉及到两层并行化：第一层是通过`CompletableFuture`实现的异步执行，第二层是通过`parallelStream`实现的数据流并行处理。下面是对代码的具体解析：

        1. **异步执行任务**：
        代码首先通过`CompletableFuture.supplyAsync()`方法为每个`TableMetaInfo`对象提交一个异步任务。这些异步任务用于评估每个表的数据治理指标。
        `CompletableFuture`在Java 8引入，提供了一种简便的方式来异步执行任务，并且可以在任务完成时获取结果，处理异常或者链式地连接其他任务。

        2. **并行流处理**：在每个异步任务内部，代码使用了`parallelStream()`来并行处理一系列的数据治理指标（`GovernanceMetric`）。
        `parallelStream()`是Java 8的Stream API的一部分，它允许以并行方式处理集合。在这个场景中，它被用来并行地对一组指标进行评估。这意味着如果有多个指标需要评估，它们将被分配到不同的线程上进行处理，这样可以显著提高处理速度，尤其是在处理大量数据时。

        3. **任务提交到线程池**：`supplyAsync()`方法的第二个参数是一个线程池（`pool`），这意味着所有异步任务都将提交到这个线程池中执行。
        这可以更有效地管理线程资源，避免创建过多的线程导致的资源耗尽问题。

        4. **收集结果**：使用`CompletableFuture.allOf(result).join();`等待所有异步任务完成。这里`allOf`是非阻塞的，
        但随后的`join`调用会阻塞当前线程，直到所有的`CompletableFuture`任务都执行完成。

        5. **结果合并**：最后，代码通过遍历所有的`CompletableFuture`对象，调用`get()`方法获取每个异步任务的结果（这里是`List<GovernanceAssessDetail>`），
        并将这些结果添加到最终的结果列表`details`中。

        这种结合使用`CompletableFuture`和`parallelStream`的方式可以有效地并行处理多任务，尤其是在需要对大量数据进行复杂处理时，
        能够充分利用多核CPU的计算资源，提高程序的执行效率。不过，需要注意的是，并行流和异步任务的过度使用可能会导致线程竞争和资源不足，因此需要根据实际情况合理配置线程池和控制并行级别。
     */
    private List<GovernanceAssessDetail> parallelAssess(List<TableMetaInfo> metaInfo, List<GovernanceMetric> metrics, String assessDate) throws ExecutionException, InterruptedException {
        /*
            为每个表创建一个线程去处理
            每个线程在考评N个指标时，使用并行流
         */
        CompletableFuture[] result = new CompletableFuture[metaInfo.size()];
        /*
            CompletableFuture.supplyAsync(): 向线程池提交一个任务，至于任务什么时候完成，无法预测
            这个任务总会在未来的某个时刻完成
         */
        List<GovernanceAssessDetail> details = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            TableMetaInfo tableMetaInfo = metaInfo.get(i);

            result[i] = CompletableFuture
                    // 异步提交
                    .supplyAsync(new Supplier<List<GovernanceAssessDetail>>() {

                        @Override
                        public List<GovernanceAssessDetail> get() {
                            // 执行N个指标的考核
                            return metrics.parallelStream() // 并行流执行人任务逻辑
                                    .map(metric -> {
                                        AssessorTemplate t =  context.getBean(metric.getMetricCode() ,AssessorTemplate.class);
                                        GovernanceAssessDetail assessDetail = t.assess(new AssessParam(assessDate, tableMetaInfo, metric));
                                        return assessDetail;
                                    })
                                    .collect(Collectors.toList());
                        }
                    }, pool);
        }
        // 等待数据中所有任务都执行完成 join会阻塞
        CompletableFuture.allOf(result).join();

        for (CompletableFuture<List<GovernanceAssessDetail>> completableFuture : result) {
            List<GovernanceAssessDetail> o = completableFuture.get();
            details.addAll(o);
        }

        return details;
    }

    // 用join查询两个表table_meta_info table_meta_info_extra
    /*
        正常应该使用baseMapper，但是这个dao是处理这个governance_assess_detail表的，
        所以我们要重新引入之前操作table_meta_info表的mapper, 但需要通过service来创建里面的方法
     */
    @Autowired
    private TableMetaInfoService metaInfoService;
    private List<TableMetaInfo> queryMetaInfo(String db, String assessDate) {

        return metaInfoService.queryMetaInfo(db,assessDate);
    }

    @Autowired
    private GovernanceMetricService metricService;
}
