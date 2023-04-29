package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 執行DML操作(INSERT)
 * DML : INSERT、UPDATE、DELETE
 */
public class JDBCDemo2 {
    public static void main(String[] args) {
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

            //4.輸入SQL語句 : DML語句(插入一條數據)

//            String sql = "INSERT INTO userinfo(username,password,nickname,age)\n" +
//                    "VALUES ('張三','123456','阿三',22);";
            /**
             * 擴展動態生成
             */

            //建立Scanner對象，並接收用戶所輸入的值
            Scanner scanner = new Scanner(System.in);
            System.out.println("歡迎註冊");
            System.out.println("請輸入用戶名稱");
            String username = scanner.nextLine();
            System.out.println("請輸入用戶密碼");
            String password = scanner.nextLine();
            System.out.println("請輸入用戶暱稱");
            String nickname = scanner.nextLine();
            System.out.println("請輸入用戶年齡");
            int age = scanner.nextInt();

            //動態建立SQL語句(要特別注意單引號的拼接)
            String sql = "INSERT INTO userinfo(username,password,nickname,age)\n" +
                    "VALUES ('" + username + "','" + password + "','" + nickname + "','" + age + "');";

            //5.執行該SQL語句
            /**
             * statement提供了多種執行SQL語句的方法
             * int executeUpdate(String sql)
             * 該方法用於數據庫可以執行DML語句
             *
             * 該方法返回一個int值，用以代表執行結果總共'更新'多少條數據
             */
            int num = statement.executeUpdate(sql);

            //驗證
            if (num > 0) {//至少成功更新了一條數據
                System.out.println("數據更新(插入)成功");
            } else {//更新零條數據(不全然代表更新失敗，程序上更新失敗會直接報錯)
                System.out.println("數據更新(插入)失敗");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
