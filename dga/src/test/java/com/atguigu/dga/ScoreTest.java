package com.atguigu.dga;

import com.atguigu.dga.score.service.GovernanceAssessGlobalService;
import com.atguigu.dga.score.service.GovernanceAssessTableService;
import com.atguigu.dga.score.service.GovernanceAssessTecOwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @date 2024-02-04
 */
@SpringBootTest
public class ScoreTest {
    @Autowired
    private GovernanceAssessTableService tableService;
    @Autowired
    private GovernanceAssessTecOwnerService tecOwnerService;
    @Autowired
    private GovernanceAssessGlobalService globalService;

    @Test
    public void tableScoreTest(){
        tableService.calTableScore("gmall","2023-05-26");
    }

    @Test
    public void OwnnerScoreTest(){
        tecOwnerService.calOwnnerScore("2023-05-26");
    }

    @Test
    public void GlobalScoreTest(){
        globalService.calGlobalScore("2023-05-26");
    }
}
