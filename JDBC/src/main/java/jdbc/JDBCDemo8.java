package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 * 模擬登入驗證業務
 * 拓展介紹注入式攻擊 : SQL injection
 * 透過輸入的訊息影響原有語意的代碼攻擊
 * 解決辦法 : 參數化查詢
 * 利用JAVA的SQL預編譯機制，先傳入SQL語句使其語意固定，再將用戶輸入的訊息以參數的方式傳入
 * 此時編譯器會將後續傳入的值以參數理解，有效避免注入式攻擊的發生
 * <p>
 * 預編譯 : 將原本需要拼接的值以"?"替代
 * "SELECT username,password,nickname " +
 * "FROM userinfo " +
 * "WHERE username = ? AND password = ? ;"
 */
public class JDBCDemo8 {
    public static void main(String[] args) {
        //從userinfo表中驗證登入用戶

        //編寫sql語句
        String sql = "SELECT username,password,nickname " +
                "FROM userinfo " +
                "WHERE username = ? AND password = ?";

        //建立語句對象
        try (
                //此時獲得的是預編譯對象
                PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(sql);
        ){
            //執行前sql要明確指明"?"為何以問號所在的順序為參數訂定類型與值
            preparedStatement.setString(1,"張三") ;//明確聲明第1個問號
            preparedStatement.setString(2, "666666");//明確聲明第2個問號

            //執行該段sql
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("登入成功，歡迎您 " + resultSet.getString("nickname"));
            } else {
                System.out.println("登入失敗，用戶名或密碼錯誤!");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
