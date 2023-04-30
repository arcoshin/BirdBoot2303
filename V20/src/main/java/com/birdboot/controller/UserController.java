package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;
import com.birdboot.util.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * 處理用戶相關業務
 * V20:導入數據庫重構
 */
@Controller
public class UserController {
    private static File userDir;

    static {
        userDir = new File("users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

    //註冊--->其實就是處理"/regUser"請求
    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("開始處理用戶註冊......");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username + "," + password + "," + nickname + "," + ageStr);

        /**
         * 必要驗證
         */
        if (
                username == null || username.isEmpty() ||
                        password == null || password.isEmpty() ||
                        nickname == null || nickname.isEmpty() ||
                        ageStr == null || ageStr.isEmpty() ||
                        !ageStr.matches("[0-9]+")
        ) {
            /**
             * 不需要每次都經過註冊功能才能訪問頁面(尤其指註冊成功時，雖然畫面相同但是網址卻不同)
             * 直接重定向可以有效節省資源
             */
            response.sendRedirect("reg_info_error.html");
            return;
        }

        int age = Integer.parseInt(ageStr);

        /**
         * V20:導入數據庫
         */
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection()
        ) {
            //編寫sql語句 : 驗證該用戶是否已存在
            String checkSQL = "SELECT 1 FROM userinfo WHERE username = ? ";

            //預編譯該語句
            PreparedStatement ps1 = connection.prepareStatement(checkSQL);

            //聲明參數
            ps1.setString(1, username);

            //執行語句
            ResultSet resultSet = ps1.executeQuery();

            //驗證結果集
            if (resultSet.next()) {//如果結果集存在，表示用戶已存在

                response.sendRedirect("reg_info_already_exist.html");

            } else {//用戶名尚未註冊，可繼續進行

                //編寫sql語句 : 新增該用戶至userinfo
                String insertSQL = "INSERT INTO userinfo (username,password,nickname,age) " +
                        "VALUES (?,?,?,?) ";

                //預編譯該語句
                PreparedStatement ps2 = connection.prepareStatement(insertSQL);

                //聲明參數
                ps2.setString(1, username);
                ps2.setString(2, password);
                ps2.setString(3, nickname);
                ps2.setInt(4, age);

                //執行語句
                int num = ps2.executeUpdate();

                //驗證更新數量
                if (num > 0) {
                    response.sendRedirect("reg_success.html");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //登入:--->其實就是處理"/loginUser"請求
    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("開始處理用戶登入......");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        /**
         * 必要驗證
         */
        if (
                username == null || username.isEmpty() ||
                        password == null || password.isEmpty()
        ) {
            response.sendRedirect("login_info_error.html");
            return;
        }

        /**
         * 綁定users包中的對應數據
         */
        File file = new File(userDir, username + ".obj");
        if (!file.exists()) {//綁定不到
            response.sendRedirect("login_info_not_exist.html");
            return;
        }

        /**
         * 通過輸入流讀入該數據
         */
        try (
                ObjectInputStream ois =
                        new ObjectInputStream(
                                new BufferedInputStream(
                                        new FileInputStream(file)
                                )
                        );

        ) {
            User user = (User) ois.readObject();

            /**
             * 比對數據
             */
            if (
                    user.getUsername().equals(username) && user.getPassword().equals(password)
            ) {
                response.sendRedirect("login_success.html");
                return;
            } else {
                response.sendRedirect("login_info_not_match.html");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
