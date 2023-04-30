package com.birdboot.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 用來管理數據庫連接的類
 */
public class DBUtil {

    //加載驅動(不同數據庫，Driver的內容不同)
    static {//只需加載一次，不需要隨著調用方法每次加載
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        //與數據庫進行連接(獲得'連接對象')
        Connection connection = DriverManager.getConnection(
                //url格式是固定的       數據庫地址  /數據庫名?參數...
                "jdbc:mysql://localhost:3306/birdbootdb?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Taipei&rewriteBatchedStatements=true",
                "root",
                "root"
        );

        return connection;
    }


}
