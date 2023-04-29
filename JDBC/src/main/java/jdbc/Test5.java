package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 使用預編譯修改用戶密碼
 * 程序要求輸入用戶名與新密碼
 */
public class Test5 {
    public static void main(String[] args) {
        System.out.println("修改密碼業務......");
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入用戶名");
        String username = scanner.nextLine();
        System.out.println("請輸入新密碼");
        String password = scanner.nextLine();

        //編寫sql語句
        String sql = "UPDATE userinfo " +
                "SET password = ? " +
                "WHERE username = ? ";

        try (
                //連接數據庫並聲明語句對象
                PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(sql);
        ) {
            //執行前明確聲明參數
            preparedStatement.setString(1,password);
            preparedStatement.setString(2,username);

            //執行語句
            int num = preparedStatement.executeUpdate();

            //獲取結果集的內容
            if (num > 0) {
                System.out.println("密碼修改成功");
            } else {
                System.out.println("密碼修改失敗");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
