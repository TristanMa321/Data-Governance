package com.atguigu.dga.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @date 2024-01-29
 */
@RequestMapping("/tableMetaInfo")
@RestController
public class MetaController {
    @Autowired
    private TableMetaInfoService metaInfoService;
    @Autowired
    private TableMetaInfoExtraService metaInfoExtraService;
    @PostMapping("/init-tables/{db}/{date}")
    public Object updateMetaInfoManual(@PathVariable("db") String db,@PathVariable("date") String date) throws IOException, MetaException {
        metaInfoService.initMetaInfo(db, date);
        metaInfoExtraService.initMetaInfoExtra(db);
        return "success";
    }

    @GetMapping("/table/{tableMetaInfoId}")
    public Object getMetaInfoDetail(@PathVariable("tableMetaInfoId") String id) throws IOException, MetaException {
        // 根据id查询tableMetaInfo(封装内置的Extra bean)
        TableMetaInfo metaInfo = metaInfoService.getById(id);
        QueryWrapper<TableMetaInfoExtra> queryWrapper = new QueryWrapper<TableMetaInfoExtra>()
                .eq("table_name", metaInfo.getTableName())
                .eq("schema_name", metaInfo.getSchemaName());
        TableMetaInfoExtra metaInfoExtra = metaInfoExtraService.getOne(queryWrapper);
        metaInfo.setTableMetaInfoExtra(metaInfoExtra);

        return metaInfo;
    }
    // @RequestBody: 将传入的JSON数据会自动转换成后面写的对象
    @PostMapping("/tableExtra")
    public Object updateExtraInfo(@RequestBody TableMetaInfoExtra extra) throws IOException, MetaException {
        // 补充一个修改时间
        extra.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        // 写入数据库
        metaInfoExtraService.saveOrUpdate(extra);
        return "success";
    }
    /**
     *
     * @param pageNo
     * @param pageSize  显示每一页的数据行数
     * @param tableName
     * @param schemaName
     * @param dwLevel   hive中哪一层
     * @return
     * @throws IOException
     * @throws MetaException
     *
     * 返回 pageSize 条。
     *      如果pageNo = 1,pageSize = 10 。 返回数据index是0-9
     *      如果pageNo = 2,pageSize = 10 。 返回数据index是10-19
     * limit start,rows
     * 起始索引 start = pageSize * (pageNo - 1)
     *
     * 如果请求方希望返回: {} , 在java程序中使用 Bean，Map，JSONObject封装。
     * 如果请求方希望返回: [] , 在java程序中使用 List，JSONArray封装。
     *     返回的json的属性名必须和请求方要求的名字一致。
     */
    @GetMapping("/table-list")
    public Object queryMetaInfoList(Integer pageNo,
                                    Integer pageSize,
                                    String tableName,
                                    String schemaName,
                                    String dwLevel) throws IOException, MetaException {
        int start = pageSize * (pageNo - 1);    // 起始索引
        // 查询符合条件的数据
        List<PageTableMetaInfo> data = metaInfoService.queryMetaInfoList(start, pageSize, tableName, schemaName, dwLevel);
        // 符合条件的表总数
        Integer total = metaInfoService.queryMetaInfoTotal(tableName, schemaName, dwLevel);
        // 要使用fastjson包下的
        JSONObject result = new JSONObject();
        result.put("total", total);
        result.put("list", data);
        return result;
    }

}
