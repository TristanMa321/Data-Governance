package com.atguigu.dga.assess.assessor.cacl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.assess.assessor.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.Field;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.TDsTaskInstance;
import com.atguigu.dga.assess.service.TDsTaskInstanceService;
import com.atguigu.dga.config.CacheUtil;
import com.atguigu.dga.config.HiveParserUtil;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**

 sql语句没有任何join|groupby|union all，且where过滤中没有非分区字段。符合以上情况给0分，其余给10分。
    说白了 就是找出数据装载非常简单的表
 -------------------------------------------------
 难点在于： 解析sql。
    1.获取到每张表调度的sql
    2.如何判断sql中是否存在join|groupby|union all？
    正则表达式可以完成
    3.获取到where中过滤的所有字段
    4.获取到当前sql查询的表，所有的分区字段
    5.判断，看where中有没有非分区字段

    需要sql解析语法树，来进行操作。
 --------------------------------------
 判断的都是调度的任务，并且要求任务有调度的sql。
    ods层的数据，和特殊表(dim_date)，这些都是load导入，没有insertsql。

 */
@Component("SIMPLE_PROCESS")
public class CheckSimpleProcess extends AssessorTemplate
{
    @Autowired
    private TDsTaskInstanceService dsService;
    @Override
    protected void assessScore(AssessParam param, GovernanceAssessDetail assessDetail) throws Exception {
        // 获取参数
        TableMetaInfo metaInfo = param.getMetaInfo();
        String schemaName = metaInfo.getSchemaName();
        TableMetaInfoExtra metaInfoExtra = metaInfo.getTableMetaInfoExtra();
        String dwLevel = metaInfoExtra.getDwLevel();
        HashSet<String> specialTable = Sets.newHashSet("dim_date", "tmp_dim_date_info","ods_log_inc");
        String tableName = metaInfo.getTableName();

        // 判断当前处理的表是不是ods层和特殊表，无需进行判断
        if (MetaConstant.DW_LEVEL_ODS.equals(dwLevel) || specialTable.contains(tableName)){
            return;
        }

        // 获取到当前表的所调度sql
        TDsTaskInstance bean = dsService.getOne(new QueryWrapper<TDsTaskInstance>()
                .eq("name", CacheUtil.getKey(metaInfo))
                .eq("date(start_time)", param.getAssessDate()));
        // System.out.println(tableName);
        String rawScript = JSON.parseObject(bean.getTaskParams()).getString("rawScript");

        String sql = parseSqlScript(rawScript.toLowerCase());

        // 把sql解析未语法树，遍历树
        MyDispatcher myDispatcher = new MyDispatcher();
        HiveParserUtil.parse(sql, myDispatcher);

        // 获取遍历完的结果
        Set<String> queryTables = myDispatcher.queryTables;     // 收集查询的表名
        Set<String> whereFilterFields = myDispatcher.whereFilterFields;     // 收集where过滤的字段
        Set<String> complexOperator = myDispatcher.complexOperator;     // 收集sql中的复杂查询
        String whereFilterFieldsString = whereFilterFields.toString();

        // 判断
        if (complexOperator.isEmpty()){
            // 再判断是否where过滤中有非分区字段
            /*
                根据查询的表名，获取这些表中分区表的所有分区字段
                 从 t1,t2,t3查询。
                    t1不是分区表
                    t2,t3是分区表，且分区的字段分别为a,b

                    where过滤的字段为: [a,b,c]

                  ---------------------
                    收集的查询的表名，可能有一些虚表(不存在的表，临时表)，例如tmp1
             */
            Set<String> partitionNames = new HashSet<>();

            for (String queryTable : queryTables) {
                TableMetaInfo tableMetaInfo = CacheUtil.metaInfoMap.get(schemaName + "." + queryTable);
                // 判断是否为虚表
                if (tableMetaInfo != null) {
                    List<Field> fields = JSON.parseArray(tableMetaInfo.getPartitionColNameJson(), Field.class);
                    for (Field field : fields) {
                        partitionNames.add(field.getName());
                    }
                }
            }
            // 差集
            whereFilterFields.removeAll(partitionNames);

            if (whereFilterFields.isEmpty()){
                //说明where过滤中没有非分区字段
                setScore(assessDetail, BigDecimal.ZERO,"当前sql是简单加工",
                        "没有复杂查询，且where过滤中除了分区字段外没有其他字段",
                        false,null);
            }
        }

        // 为每一个考评的表，添加解析的结果
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("查询的表", queryTables);
        jsonObject.put("复杂运算", complexOperator);
        jsonObject.put("过滤的字段", whereFilterFieldsString);

        assessDetail.setAssessComment(jsonObject.toJSONString());
    }

    /*
        两种书写格式：
            CTE：  with xxxx as ()
                  insert xxx;

            非CET: insert xxxx

         从rawScript以(with|insert)为开头，到结束(;|")
     */

    private String parseSqlScript(String rawScript) {
        // 确认起始位置
        int start = rawScript.indexOf("with");
        if (start == -1){
            // 不是cte写法
            start = rawScript.indexOf("insert");
        }
        // 确定结束位置(从上面的起始位置之后开始找)
        int end = rawScript.indexOf(";",start);
        if (end == -1){
            // 没有; 找"号
            end = rawScript.indexOf("\"",start);
        }
        return rawScript.substring(start, end);
    }

    /*
         1.如何判断sql中是否存在join|groupby|union all？
                    遍历语法树，看是否有节点为 TOK_UNIONALL | TOK_GROUPBY .....

         2.如何获取到where中过滤的所有字段?
                先找到TOK_WHERE
                    从TOK_WHERE的子节点中找到比较运算符: = , < , >
                        遍历比较运算符的子节点，
                                判断子节点是不是.
                                    如果不是. ,不断遍历，找到TOK_TABLE_OR_COL，再获取子节点
                                    如果是.找到.节点的第二个孩子

         3.如何获取到当前sql查询的表名？
             总结：    找到 TOK_TABNAME节点，
                                    子节点数量为1，获取第一个子节点的名字，
                                    子节点数量为2，获取第二个子节点的名字。
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyDispatcher implements Dispatcher{
        // 收集sql中的复杂查询
        private Set<String> complexOperator = new HashSet<>();
        // 收集where过滤的字段
        private Set<String> whereFilterFields = new HashSet<>();
        // 收集查询的表名
        private Set<String> queryTables = new HashSet<>();

        //使用这个集合判断当前节点是不是复杂查询
        Set<Integer> complexOperatorSet= Sets.newHashSet(
                HiveParser.TOK_JOIN,  //join 包含通过where 连接的情况
                HiveParser.TOK_GROUPBY,       //  group by
                HiveParser.TOK_LEFTOUTERJOIN,       //  left join
                HiveParser.TOK_RIGHTOUTERJOIN,     //   right join
                HiveParser.TOK_FULLOUTERJOIN,     // full join
                HiveParser.TOK_FUNCTION,     //count(1)
                HiveParser.TOK_FUNCTIONDI,  //count(distinct xx)
                HiveParser.TOK_FUNCTIONSTAR, // count(*)
                HiveParser.TOK_SELECTDI,  // distinct
                HiveParser.TOK_UNIONALL   // union ,union all
        );

        //定义了所有的比较运算符
        Set<String> compareOperators= Sets.newHashSet("=",">","<",">=","<=" ,"<>" ,"!=" ,"like","not like"); // in / not in 属于函数计算

        /*
            GraphWalker走到树形图的每一个节点后，干什么事情
                从Node对象中可以获取:
                    name（String类型） = type（Int类型） : 整数
                    text: 字符串，TOK_XXX
         */
        @Override
        public Object dispatch(Node node, Stack<Node> stack, Object... nodeOutputs) throws SemanticException {

            ASTNode astNode = (ASTNode) node;

            // 判断当前节点是不是一个复杂运算
            if (complexOperatorSet.contains(astNode.getType())){
                complexOperator.add(astNode.getText());
            }else if (astNode.getType() == HiveParser.TOK_TABNAME) {  // 判断当前节点是不是一个表名节点
                ArrayList<Node> subNodes = astNode.getChildren();
                if (subNodes.size() == 1){
                    queryTables.add(astNode.getChild(0).getText());
                }else {
                    queryTables.add(astNode.getChild(1).getText());
                }
            }else if (astNode.getType() == HiveParser.TOK_WHERE){
                // 判断当前节点是一个where过滤节点
                // 获取比较运算符
                List<ASTNode> compareOperatorNodes = new ArrayList<>();
                parseCompareOperator(astNode, compareOperatorNodes);
                // 对于每一个比较运算符节点，尝试获取下面的where过滤字段
                for (ASTNode compareOperatorNode : compareOperatorNodes) {
                    parseWhereFilterFields(compareOperatorNode);
                }
            }
            return null;
        }

        private void parseWhereFilterFields(ASTNode compareOperatorNode) {
            ArrayList<Node> children = compareOperatorNode.getChildren();
            // 添加退出递归的条件
            if (children == null || children.isEmpty()  ){
                return;
            }
            for (Node child : children) {
                ASTNode astChild = (ASTNode) child;
                if (astChild.getType() == HiveParser.DOT){
                    whereFilterFields.add(astChild.getChild(1).getText());
                }else if (astChild.getType() == HiveParser.TOK_TABLE_OR_COL){
                    whereFilterFields.add(astChild.getChild(0).getText());
                }else {
                    parseWhereFilterFields(astChild);
                }
            }
        }

        // 从where节点不断向下遍历，找到where节点中所有的比较运算符节点
        private void parseCompareOperator(ASTNode whereNode, List<ASTNode> compareOperatorNodes) {
            ArrayList<Node> children = whereNode.getChildren();
            // 添加退出递归的条件
            if (children == null || children.isEmpty()  ){
                return;
            }
            for (Node child : children) {
                ASTNode astChild = (ASTNode) child;
                // 如果当前节点就是比较运算符
                if (compareOperators.contains(astChild.getText())){
                    compareOperatorNodes.add(astChild);
                }else {
                    // 继续递归下一级
                    parseCompareOperator(astChild, compareOperatorNodes);
                }
            }
        }
    }
}
