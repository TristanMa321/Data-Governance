package com.atguigu.dga.assess.assessor.spec;

import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 参考建数仓表规范
 ODS层 ：开头:ods  结尾 :inc/full
 结构ods_xx_( inc|full)
 DIM层 :  dim开头     full/zip 结尾
 结构: dim_xx_( full/zip)
 DWD层:  dwd 开头  inc/full 结尾
 结构： dwd_xx_xx_(inc|full)
 DWS层： dws开头
 结构dws_xx_xx_xx_ (1d/nd/td)
 ADS层： ads 开头
 结构 ads_xxx
 符合则 10分，否则0分
 OTHER：
 未纳入分层，给5分

 核心： 根据表的分层，和每一层命名的模式进行比对，看是否符合。
 */
@Component("TABLE_NAME_STANDARD")
public class CheckTableNameLegal extends AssessorTemplate
{
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) {
        // 取参数 表名，层级
        TableMetaInfo metaInfo = param.getMetaInfo();
        String dwLevel = metaInfo.getTableMetaInfoExtra().getDwLevel();
        String tableName = metaInfo.getTableName().toLowerCase();
        // 判断层级
        if(MetaConstant.DW_LEVEL_UNSET.equals(dwLevel)){
            setScore(assessDetail, BigDecimal.ZERO, "未写层级","",true, metaInfo.getId());
        }else if (MetaConstant.DW_LEVEL_OTHER.equals(dwLevel)){
            setScore(assessDetail, BigDecimal.valueOf(5), "写了other层级","",true, metaInfo.getId());
        }else {
            switch (dwLevel){
                case MetaConstant.DW_LEVEL_ODS:checkTableName(tableName,MetaConstant.ODS_REGEX,assessDetail,metaInfo.getId());break;
                case MetaConstant.DW_LEVEL_DIM:checkTableName(tableName,MetaConstant.DIM_REGEX,assessDetail,metaInfo.getId());break;
                case MetaConstant.DW_LEVEL_DWD:checkTableName(tableName,MetaConstant.DWD_REGEX,assessDetail,metaInfo.getId());break;
                case MetaConstant.DW_LEVEL_DWS:checkTableName(tableName,MetaConstant.DWS_REGEX,assessDetail,metaInfo.getId());break;
                case MetaConstant.DW_LEVEL_ADS:checkTableName(tableName,MetaConstant.ADS_REGEX,assessDetail,metaInfo.getId());break;
            }
        }
    }

    // 使用正则表达式 检测表名
    public void checkTableName(String tname, String regexStr, GovernanceAssessDetail assessDetail, Long id){
        // 把表达式变成一个对象
        Pattern pattern = Pattern.compile(regexStr);
        // 调用正则表达式检测
        Matcher matcher = pattern.matcher(tname);
        // 匹配
        if (!matcher.matches()){
            setScore(assessDetail, BigDecimal.ZERO, "表名命名不合规","",true,id);
        }

    }
}
