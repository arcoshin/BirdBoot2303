package com.birdboot.util;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 用來管理數據庫連接的類
 */
public class DBUtil {
    //調用連接池
    private static DruidDataSource ds;

    //加載驅動(不同數據庫，Driver的內容不同)，使用連接池技術則會自動加載驅動
    static {//只需加載一次，不需要隨著調用方法每次加載
        initDataSource();//代碼整潔
    }

    private static void initDataSource(){//DruidDataSource就是套了一個殼的概念
        ds = new DruidDataSource();
        ds.setUsername("root");
        ds.setPassword("root");
        ds.setUrl("jdbc:mysql://localhost:3306/birdbootdb?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Taipei&rewriteBatchedStatements=true");
        ds.setMaxActive(30);//與數據庫的同時最大連接數
        ds.setInitialSize(5);//與數據庫的初始連接數
    }



    public static Connection getConnection() throws SQLException {
        //與數據庫進行連接(獲得'連接對象')

        return ds.getConnection();
    }


}
