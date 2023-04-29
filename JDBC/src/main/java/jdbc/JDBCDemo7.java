package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 模擬登入驗證業務
 * 拓展介紹注入式攻擊 : SQL injection
 * 透過輸入的訊息影響原有語意的代碼攻擊
 */
public class JDBCDemo7 {
    public static void main(String[] args) {
        //從userinfo表中驗證登入用戶
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入用戶名");
        String username = scanner.nextLine();
        System.out.println("請輸入密碼");
        String password = scanner.nextLine();

        //編寫sql語句
        String sql = "SELECT username,password,nickname " +
                "FROM userinfo " +
                "WHERE username = '" + username + "' AND password ='" + password + "'; ";

        //建立語句對象
        try (
                ResultSet resultSet = DBUtil.getConnection().createStatement().executeQuery(sql)
        ) {
            if (resultSet.next()) {
                System.out.println("登入成功，歡迎您 " + resultSet.getString("nickname"));
            } else {
                System.out.println("登入失敗，用戶名或密碼錯誤!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
