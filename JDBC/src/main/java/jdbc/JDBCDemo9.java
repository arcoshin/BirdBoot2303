package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 * 模擬註冊業務
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
public class JDBCDemo9 {
    public static void main(String[] args) {
        //從userinfo表中實現註冊用戶
        System.out.println("歡迎註冊");
        Scanner scanner = new Scanner(System.in);

        System.out.println("請輸入用戶名稱");
        String username = scanner.nextLine();
        System.out.println("請輸入用戶密碼");
        String password = scanner.nextLine();
        System.out.println("請輸入用戶暱稱");
        String nickname = scanner.nextLine();
        System.out.println("請輸入用戶年齡");
        int age = scanner.nextInt();

        //編寫sql語句
        String sql = "INSERT INTO userinfo (username,password,nickname,age) " +
                "VALUES (?,?,?,?) ";//"?"僅能替代"值"

        //建立語句對象
        try (
                //此時獲得的是預編譯對象
                PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(sql);
        ) {
            //執行前sql要明確指明"?"為何以問號所在的順序為參數訂定類型與值
            preparedStatement.setString(1,username);//明確聲明第1個問號替代的值
            preparedStatement.setString(2,password);//明確聲明第2個問號替代的值
            preparedStatement.setString(3,nickname);//明確聲明第3個問號替代的值
            preparedStatement.setInt(4,age);//明確聲明第4個問號替代的值

            //執行該段sql
            int num = preparedStatement.executeUpdate();

            if (num > 0) {
                System.out.println("註冊成功");
            } else {
                System.out.println("註冊失敗");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
