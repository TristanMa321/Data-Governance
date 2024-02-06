package com.atguigu.dga.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import com.atguigu.dga.config.CalScoreTask;
import com.atguigu.dga.score.bean.GovernanceAssessGlobal;
import com.atguigu.dga.score.bean.GovernanceAssessTecOwner;
import com.atguigu.dga.score.service.GovernanceAssessGlobalService;
import com.atguigu.dga.score.service.GovernanceAssessTableService;
import com.atguigu.dga.score.service.GovernanceAssessTecOwnerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @date 2024-02-05
 */
@RestController
@RequestMapping("/governance")
public class ScoreController {
    @Autowired
    private GovernanceAssessTableService tableService;
    @Autowired
    private GovernanceAssessTecOwnerService ownerService;
    @Autowired
    private GovernanceAssessGlobalService globalService;
    @Autowired
    private GovernanceAssessDetailService detailService;

    public String  getLastAssessDate(){
        // 全局考评分数每天只有一行，所以只要取日期列的最大值
         return globalService.getOne(
                new QueryWrapper<GovernanceAssessGlobal>()
                        .select("max(assess_date) assessDate")
        ).getAssessDate();
    }
    /*
        {  "assessDate":"2023-04-01" ,"sumScore":90, "scoreList":[20,40,34,55,66] }
        -- 规范,存储，计算，质量，安全
     */
    @GetMapping("/globalScore")  //全局分数接口
    public Object getGlobalScore(){
        String assessDate = getLastAssessDate();

        GovernanceAssessGlobal global = globalService.getOne(new QueryWrapper<GovernanceAssessGlobal>()
                .eq("assess_date", assessDate));

        // 封装数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("assessDate", assessDate);
        jsonObject.put("sumScore", global.getScore());
        jsonObject.put(
                "scoreList",
                Arrays.asList(
                        global.getScoreSpec(),
                        global.getScoreStorage(),
                        global.getScoreCalc(),
                        global.getScoreQuality(),
                        global.getScoreSecurity()
                )
        );
        return jsonObject;
    }

    @GetMapping("/problemList/{governType}/{pageNo}/{pageSize}")
    public  Object getProblemList(@PathVariable("governType") String type,
                                  @PathVariable("pageNo") Integer pageNo,
                                  @PathVariable("pageSize") Integer size){
        //计算返回的数据的起始索引
        int from = (pageNo -1 ) * size;
        String assessDate = getLastAssessDate();

        return detailService.list(
                new QueryWrapper<GovernanceAssessDetail>()
                        .eq("assess_date", assessDate)
                        .eq("governance_type", type)
                        .lt("assess_score", 10)
                        .orderByAsc("id")
                        .last("limit "+ from + "," + size)
        );
    }
    /*
        {"SPEC":1, "STORAGE":4,"CALC":12,"QUALITY":34,"SECURITY":12}
     */
    @GetMapping("/problemNum")
    public Object getProblemNum(){
        String assessDate = getLastAssessDate();

        List<GovernanceAssessDetail> list = detailService.list(
                new QueryWrapper<GovernanceAssessDetail>()
                        .eq("assess_date", assessDate)
                        .groupBy("governance_type")
                        .lt("assess_score", 10)
                        .select("governance_type", "count(id) id")
        );
        JSONObject jsonObject = new JSONObject();
        for (GovernanceAssessDetail detail : list) {
            jsonObject.put(detail.getGovernanceType(),detail.getId());
        }

        return jsonObject;
    }

    /*
        [{"tecOwner":"zhang3" ,"score":99},
        {"tecOwner":"li4" ,"score":98},
        {"tecOwner": "wang5","score":97}   ]
     */
    @GetMapping("/rankList")
    public Object getRankList(){
        String assessDate = getLastAssessDate();

        List<GovernanceAssessTecOwner> list = ownerService.list(
                new QueryWrapper<GovernanceAssessTecOwner>()
                        .eq("assess_date", assessDate)
                        .isNotNull("tec_owner")
                        .ne("tec_owner", "") // 确保tec_owner不为空字符
                        .select("tec_owner", "score")
        );

        // 自己手动吧GovernanceAssessTecOwner转为json格式，只要GovernanceAssessTecOwner中的tecOwner列和score
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("tecOwner", "score");

        return JSON.toJSONString(list, filter);
    }

    @Autowired
    private CalScoreTask calScoreTask;
    @PostMapping("/assess/{date}")
    public Object reAssess(@PathVariable("date") String date) throws IOException, MetaException, ExecutionException, InterruptedException {
        calScoreTask.governanceAssess(date);
        return "success";
    }
}
