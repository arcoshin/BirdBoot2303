package jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 刪除指定用戶
 * 程序啟動後要求用戶輸入用戶名，然後將userinfo表中的該用戶進行刪除
 * <p>
 * DELETE FROM userinfo
 * WHERE username='?'
 */
public class Test1 {
    public static void main(String[] args) {
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();
        ) {
            //獲取欲刪除的用戶名稱
            System.out.println("請輸入要刪除的用戶名");
            String username = new Scanner(System.in).nextLine();

            //編寫sql語句
            String sql = "DELETE " +
                    "FROM userinfo " +
                    "WHERE username = '" + username + "';";

            //聲明語句對象
            Statement statement = connection.createStatement();

            //執行語句
            int num = statement.executeUpdate(sql);

            //驗證
            if (num > 0) {
                System.out.println("刪除成功");
            } else {
                System.out.println("刪除失敗");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("請輸入要刪除用戶的名字");
//        String name = scanner.nextLine();
//
//        try (
//                Connection connection = DBUtil.getConnection();
//        ){
//            Statement statement = connection.createStatement();
//            String sql = "DELETE FROM userinfo " +
//                         "WHERE username='"+name+"'";
//            int num = statement.executeUpdate(sql);
//            if(num>0){
//                System.out.println("刪除成功");
//            }else{
//                System.out.println("刪除失敗");
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
}
