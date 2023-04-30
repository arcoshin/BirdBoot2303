package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;
import com.birdboot.util.DBUtil;

import java.io.*;
import java.sql.*;


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

        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();
        ) {
            //編寫sql指令
            String sql = "SELECT username,password " +
                    "FROM userinfo " +
                    "WHERE username = ?";

            //預編譯語句對象
            PreparedStatement prepareStatement = connection.prepareStatement(sql);

            //聲明參數
            prepareStatement.setString(1, username);

            //執行語句
            ResultSet resultSet = prepareStatement.executeQuery();

            //驗證結果集
            if (!resultSet.next()) {//如果結果集內數據不存在

                //重定向用戶不存在
                response.sendRedirect("login_info_not_exist.html");

            } else {//如果結果集內數據存在

                //比對密碼
                if (password.equals(resultSet.getString("password"))) {
                    //比對成功
                    response.sendRedirect("login_success.html");
                } else {
                    //比對失敗
                    response.sendRedirect("login_info_not_match.html");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //清單:--->其實就是處理"/userList"請求
    @RequestMapping("/userList")
    public void userList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("開始生成用戶列表......");
        response.addHeaders("Content-Type","text/html;charset=utf-8");


//
//        try (Connection connection = DBUtil.getConnection();){
//            response.addHeaders("ContentType","text/html;charset=utf-8");
//            PrintWriter pw = response.getWriter();
//            pw.println("<!DOCTYPE html>");
//            pw.println("<html lang=\"en\">");
//            pw.println("<head>");
//            pw.println("<meta charset=\"UTF-8\">");
//            pw.println("<title>用户列表</title>");
//            pw.println("</head>");
//            pw.println("<body>");
//            pw.println("<center>");
//            pw.println("<h1>用户列表</h1>");
//            pw.println("<table border=\"1\">");
//            pw.println("<tr>");
//            pw.println("<td>ID</td>");
//            pw.println("<td>用户名</td>");
//            pw.println("<td>密码</td>");
//            pw.println("<td>昵称</td>");
//            pw.println("<td>年龄</td>");
//            pw.println("<td>操作</td>");
//            pw.println("</tr>");
//
//            String sql = "SELECT id,username,password,nickname,age FROM userinfo";
//            Statement statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()){
//                int id = rs.getInt("id");
//                pw.println("<tr>");
//                pw.println("<td>"+id+"</td>");
//                pw.println("<td>"+rs.getString("username")+"</td>");
//                pw.println("<td>"+rs.getString("password")+"</td>");
//                pw.println("<td>"+rs.getString("nickname")+"</td>");
//                pw.println("<td>"+rs.getInt("age")+"</td>");
//                pw.println("<td><a href='/deleteUser?id="+id+"'>删除</a></td>");
//                pw.println("</tr>");
//            }
//
//            pw.println("</table>");
//            pw.println("</center>");
//            pw.println("</body>");
//            pw.println("</html>");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
