package com.atguigu.dga.meta.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageTableMetaInfo
{
    //元数据信息id
    private Integer id;
    private String tableName;
    private String schemaName;
    //来自table_meta_info_extra
    private String tecOwnerUserName;
    private String busiOwnerUserName;
    //来自table_meta_info
    private String tableComment;
    private long tableSize;
    private long tableTotalSize;
    private Timestamp tableLastAccessTime;
    private Timestamp tableLastModifyTime;
}