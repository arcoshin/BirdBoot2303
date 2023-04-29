package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 執行DQL操作
 * DQL : SELECT
 */
public class JDBCDemo4 {
    public static void main(String[] args) {
        try (
                //1~2.獲取連接:Connection實現了AutoCloseable
                Connection connection = DBUtil.getConnection();

                //3.通過'連接對象'創建用於執行SQL語句的'語句對象'
                Statement statement = connection.createStatement();
        ) {

            //4.輸入SQL語句 : DQL語句(查詢數據)

            String sql = "SELECT id,name,salary,title " +
                    "FROM teacher;";

            /**
             * statement提供了多種執行SQL語句的方法
             * ResultSet executeQuery(String sql)
             * 該方法用於數據庫可以執行DQL語句
             *
             * 該方法返回一個結果集ResultSet，用以代表執行結果(表格)
             */
            ResultSet resultSet = statement.executeQuery(sql);

            /**
             * ResultSet提供了很多重要的方法
             * boolean next();讓結果集的指針移至下一行數據，並判定其是否存在(跟迭代器不同)
             */
            while (resultSet.next()) {//結果集向下移動一行，並判斷該行是否存在
                /**
                 * ResultSet提供了很多獲取參數的方法
                 * int getInt("字段名") 獲取某個字段的數據並以整數形式返回
                 * String getString("字段名") 獲取某個字段的數據並以字符串形式返回
                 * .
                 * .
                 */
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int salary = resultSet.getInt("salary");
                String title = resultSet.getString("title");

                //輸出
                System.out.println(id + "," + name + "," + salary + "," + title);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
