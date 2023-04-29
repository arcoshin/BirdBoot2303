package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 程序啟動後要求輸入班級名稱
 * 然後列出該班級的id，班級名，所在樓層
 * SELECT id,name,floor
 * FROM class
 * WHERE name='?'
 */
public class Test3 {
    public static void main(String[] args) {
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();

                //聲明語句對象
                Statement statement = connection.createStatement();
        ) {
            //獲取用戶數據
            System.out.println("請輸入所要查詢的班級");
            String className = new Scanner(System.in).nextLine();

            //編寫sql語句
            String sql = "SELECT id,name,floor " +
                    "FROM class " +
                    "WHERE name = '" + className + "';";

            //執行語句
            ResultSet resultSet = statement.executeQuery(sql);

            //獲取結果集的內容
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int floor = resultSet.getInt("floor");
                System.out.println(id + "," + name + "," + floor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("請輸入班級名稱");
//        String name = scanner.nextLine();
//
//        try(Connection connection = DBUtil.getConnection();) {
//            Statement statement = connection.createStatement();
//            String sql = "SELECT id,name,floor " +
//                         "FROM class " +
//                         "WHERE name='"+name+"'";
//            ResultSet rs = statement.executeQuery(sql);
//            while(rs.next()){
//                int id = rs.getInt("id");
//                String cname = rs.getString("name");
//                int floor = rs.getInt("floor");
//                System.out.println(id+","+cname+","+floor);
//            }
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
}
