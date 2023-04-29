package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Java DataBase Connectivity
 * JDBC是JAVA官方提供的一套接口，用於連接數據庫
 * 核心接口:
 * Connection           表示數據庫連接
 * Statement            用來執行SQL語句的語句對象
 * PreparedStatement    用來執行預編譯SQL語句的語句對象
 * ResultSet            用來表示查詢結果集
 * <p>
 * 不同DBMS都會提供一套具體的實現類，並打包為JAR包
 * 這個JAR包稱為DBMS的驅動
 */
public class JDBCDemo1 {
    public static void main(String[] args) {
        /**
         * JDBC提供了連接、操作數據的流程
         * 1.加載驅動(於pom.xml配置依賴)
         * 2.通過驅動管理器:DriverManager與DBMS建立連接(返回一個Connection)
         * 3.通過連接對象創建語句對象Statement
         * 4.通過語句對象執行相應的SQL語句給數據庫
         * 5.獲得執行SQL語句的結果
         */

        try {
            //1.加載驅動(不同數據庫，Driver的內容不同)
            Class.forName("com.mysql.cj.jdbc.Driver");


            //2.與數據庫進行連接(獲得'連接對象')
            Connection connection = DriverManager.getConnection(
                    //url格式是固定的       數據庫地址  /數據庫名?參數...
                    "jdbc:mysql://localhost:3306/tedu?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Taipei&rewriteBatchedStatements=true",
                    "root",
                    "root"
            );

            //驗證
            System.out.println("與數據庫建立連接!");

            //3.通過'連接對象'創建用於執行SQL語句的'語句對象'
            Statement statement = connection.createStatement();

            //4.輸入SQL語句 : DDL語句(建立一個表)
            String sql =
                    "CREATE TABLE userinfo(\n " +
                            "id INT PRIMARY KEY AUTO_INCREMENT,\n " +
                            "username VARCHAR(30),\n " +
                            "password VARCHAR(30),\n " +
                            "nickname VARCHAR(30),\n " +
                            "age INT(3)\n " +
                            "); ";

            //5.執行該SQL語句
            /**
             * statement提供了多種執行SQL語句的方法
             * boolean execute(String sql)
             * 該方法用於數據庫可以執行任何sql語句
             * 但實際上DML、DQL都有專門的方法，因此execute通常只用來執行DDL(建庫、建表...)
             *
             * 該方法返回一個boolean值，用以代表執行結果是否帶有結果集(ResultSet)
             */
            boolean execute = statement.execute(sql);
            System.out.println(execute);//false，建表無結果集

            //6.關閉連接
            connection.close();

            //驗證
            System.out.println("表格創建成功!");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }
}
