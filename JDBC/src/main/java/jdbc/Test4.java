package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 程序啟動後要求輸入班級名稱
 * 然後列出該班級的id，班級名，所在樓層
 * SELECT id,name,floor
 * FROM class
 * WHERE name='?'
 */
public class Test4 {
    public static void main(String[] args) {
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();

                //聲明語句對象
                Statement statement = connection.createStatement();
        ) {
            //編寫sql語句
            String sql = "SELECT c.name,t.name,COUNT(*) sum " +
                    "FROM class c " +
                    "JOIN student s ON s.class_id = c.id " +
                    "JOIN teacher t ON t.id = c.teacher_id " +
                    "GROUP BY c.name ;";

            //執行語句
            ResultSet resultSet = statement.executeQuery(sql);

            //獲取結果集的內容
            while (resultSet.next()) {
                String t_name = resultSet.getString("t.name");
                String c_name = resultSet.getString("c.name");
                int sum = resultSet.getInt("sum");
                System.out.println(c_name + "," + t_name + "," + sum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
