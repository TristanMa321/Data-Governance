 package com.atguigu.dga.config;

public interface MetaConstant
{
    // 准备检测表名的正则表达式
    String ODS_REGEX = "^ods_\\w+_(inc|full)$";
    String DIM_REGEX = "^dim_\\w+_(zip|full)$";
    String DWD_REGEX = "^dwd_(trade|user|tool|traffic|interaction)_\\w+_(inc|full|acc)$";
    String DWS_REGEX = "^dws_(trade|user|tool|traffic|interaction)_\\w+_(\\d+d|nd|td)$";
    String ADS_REGEX = "^ads_\\w+$";
    String SCHEMA_NAME="";
    //存储周期
    String LIFECYCLE_TYPE_PERM="PERM";  //永久
    String LIFECYCLE_TYPE_ZIP="ZIP";   //拉链
    String LIFECYCLE_TYPE_DAY="DAY";  //日分区
    String LIFECYCLE_TYPE_UNSET="UNSET";  //未设置

    //安全级别
    String SECURITY_LEVEL_UNSET="UNSET";  //未设置
    String SECURITY_LEVEL_PUBLIC="PUBLIC";  //公开
    String SECURITY_LEVEL_INTERNAL="INTERNAL";  //内部
    String SECURITY_LEVEL_SECRET="SECRET";  //保密
    String SECURITY_LEVEL_HIGH="HIGH";  //高度机密

    //层级 对应页面上的下拉框中的选项
    String DW_LEVEL_UNSET = "UNSET";
    String DW_LEVEL_ODS = "ODS";
    String DW_LEVEL_DWD = "DWD";
    String DW_LEVEL_DWS = "DWS";
    String DW_LEVEL_DIM = "DIM";
    String DW_LEVEL_ADS = "ADS";
    String DW_LEVEL_OTHER = "OTHER";

    //DS状态码
    Integer TASK_STATE_SUCCESS = 7;
    Integer TASK_STATE_FAILED = 6;

}