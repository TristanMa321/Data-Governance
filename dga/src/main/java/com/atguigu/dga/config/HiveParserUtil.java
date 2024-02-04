package com.atguigu.dga.config;

import org.apache.hadoop.hive.ql.lib.DefaultGraphWalker;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.GraphWalker;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.SemanticException;

import java.util.Collections;
import java.util.Stack;

/**
 * @date 2024-02-02
 */
public class HiveParserUtil {
    /*
        需求：
            1.如何判断sql中是否存在join|groupby|union all？
                    遍历语法树，看是否有节点为 TOK_UNIONALL | TOK_GROUPBY .....
            2.如何获取到where中过滤的所有字段?
                    找到比较运算符: = , < , >
                        遍历比较运算符的子节点，
                                判断子节点是不是 '.'
                                    如果不是. ,不断遍历，找到TOK_TABLE_OR_COL，再获取子节点
                                    如果是.找到.节点的第二个孩子

            3.如何获取到当前sql查询的表名？

                    from 表名:  找到 TOK_TABNAME节点，获取第一个子节点的名字。
                    from 库名.表名:  找到 TOK_TABNAME节点，获取第二个子节点的名字。
                    from 表名 t1:    找到 TOK_TABNAME节点，获取第一个子节点的名字。
                    from 库名.表名 t1:    找到 TOK_TABNAME节点，获取第二个子节点的名字。

                    总结：    找到 TOK_TABNAME节点，
                                    子节点数量为1，获取第一个子节点的名字，
                                    子节点数量为2，获取第二个子节点的名字。
           --------------------------------------------
            如何遍历树？
                 Hive提供了内置的遍历树的方法，可以直接用。

                 遍历树： DefaultGraphWalker 固定代码
                 Dispatcher： 派发器，用来封装遍历到数据的每一个节点后要执行的代码逻辑。

     */

    public static void parse(String sql, Dispatcher dispatcher) throws Exception {

        //创建一个解析器
        ParseDriver parseDriver = new ParseDriver();
        //使用解析器把 字符串的sql 解析为一个语法树
        ASTNode tree = parseDriver.parse(sql);
        // 获取TOK_QUERY节点
        ASTNode query = (ASTNode)tree.getChild(0);

        //hive提供了一个遍历树的组件，只需要编写找到一个节点后执行的判断逻辑即可，而遍历操作由组件自动完成
        GraphWalker ogw = new DefaultGraphWalker(dispatcher);
        // 用遍历器遍历整个语法树
        ogw.startWalking(Collections.singletonList(query), null);

    }

    public static class MyDispatcher implements Dispatcher{

        /*
            GraphWalker走到树形图的每一个节点后，干什么事情
                从Node对象中可以获取:
                    name = type : 整数
                    text: 字符串，TOK_XXX
         */

        @Override
        public Object dispatch(Node nd, Stack<Node> stack, Object... nodeOutputs) throws SemanticException {

            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String sql = "  with tmp1 as (" +
                "  select " +
                "  t1.name,t1.age,t2.score " +
                "  from student t1 left join score t2 " +
                "  on t1.id = t2.id " +
                ") " +
                "insert overwrite table result " +
                "select " +
                "   name,sum(score) totalScore " +
                "from tmp1 " +
                "group by name " +
                "union all " +
                "select " +
                "   age,sum(score) totalScore " +
                "from tmp1 " +
                " where age > 10 " +
                "group by age " ;

        String sql2 = " with t1 as (select aa(a),b,c,dt as dd from tt1,  tt2 \n" +
                "             where tt1.a=tt2.b and dt='2023-05-11'  )\n" +
                "  insert overwrite table tt9  \n" +
                "  select a,b,c \n" +
                "  from t1 \n" +
                "  where    dt = date_add('${xxx}',-4 )    \n" +
                "  union \n" +
                "  select a,b,c \n" +
                "  from t2\n" +
                "   where    dt = date_add('${xxx} ',-7 )  ";

        String sql3 = "select * from gmall.dim_user_zip t1  where  (dt = xxxx and a = b) or c > d";

        String sql4 = "select * from dim_user_zip t1  where t1.name = 'jack' ";

        String sql5 = "select * from dim_user_zip  where name = 'jack' ";

        parse(sql5,null);
    }
}
