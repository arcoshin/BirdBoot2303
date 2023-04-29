package jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 創建一張表article
 * 表結構
 * id 整數類型，主鍵，自增           唯一標識
 * title 字符串類型，長度600字節     文章標題
 * content 字符串類型，長度3000字節  文章正文
 */
public class Test2 {
    public static void main(String[] args) {
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();

                //聲明語句對象
                Statement statement = connection.createStatement();
        ) {
            //編寫sql語句
            String sql = "CREATE TABLE article (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "title VARCHAR(600) NOT NULL," +
                    "content VARCHAR(3000) " +
                    ");";

            //執行語句
            statement.execute(sql);

            //驗證
            System.out.println("創建完成");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    public static void main(String[] args) {
//        try (Connection connection = DBUtil.getConnection();){
//            Statement statement = connection.createStatement();
//            /*
//                CREATE TABLE article(
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    title VARCHAR(600),
//                    content VARCHAR(3000)
//                )
//             */
//            String sql = "CREATE TABLE article( " +
//                         "id INT PRIMARY KEY AUTO_INCREMENT, " +
//                         "title VARCHAR(600), " +
//                         "content VARCHAR(3000) " +
//                         ")";
//            statement.execute(sql);
//            System.out.println("創建完畢");
//
//
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
}
