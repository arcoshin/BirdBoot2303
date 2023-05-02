package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.core.ClientHandler;
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
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection()
        ) {
            /**
             * 以文本格式直接對瀏覽器寫入HTML語句
             */
            System.out.println("開始生成用戶列表......");
            response.addHeaders("Content-Type", "text/html;charset=utf-8");

            /**
             * 正文開始
             */
            response.addHtmlContents("<!DOCTYPE html>");
            response.addHtmlContents("<html lang=\"en\">");
            response.addHtmlContents("<head>");
            response.addHtmlContents("<meta charset=\"UTF-8\">");
            response.addHtmlContents("<title>用戶列表</title>");
            response.addHtmlContents("</head>");
            response.addHtmlContents("<body>");
            response.addHtmlContents("<center>");
            response.addHtmlContents("<h1>用戶列表</h1>");
            response.addHtmlContents("<table border=\"1\">");
            response.addHtmlContents("<tr>");
            response.addHtmlContents("<td>ID</td>");
            response.addHtmlContents("<td>用戶名稱</td>");
            response.addHtmlContents("<td>用戶密碼</td>");
            response.addHtmlContents("<td>用戶暱稱</td>");
            response.addHtmlContents("<td>用戶年齡</td>");
            response.addHtmlContents("<td>用戶操作</td>");
            response.addHtmlContents("</tr>");


            /**
             * 這一段開始查找數據庫中所有用戶的指定數據並寫入
             */

            //編寫sql語句 : 查找數據庫中所有用戶的數據
            String sql = "SELECT id,username,password,nickname,age " +
                    "FROM userinfo ";

            //創建語句對象執行sql語句
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            //分析結果集
            while (resultSet.next()) {//將指針移到下一行數據，如果存在......
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String nickname = resultSet.getString("nickname");
                int age = resultSet.getInt("age");

                response.addHtmlContents("<tr>");
                response.addHtmlContents("<td>" + id + "</td>");
                response.addHtmlContents("<td>" + username + "</td>");
                response.addHtmlContents("<td>" + password + "</td>");
                response.addHtmlContents("<td>" + nickname + "</td>");
                response.addHtmlContents("<td>" + age + "</td>");
                response.addHtmlContents("<td><a href=\"/deleteUser?id=" + id + "\">刪除</a></td>");
                response.addHtmlContents("</tr>");
            }

            /**
             * 補齊標籤末尾
             */
            response.addHtmlContents("<a href=\"/index.html\">回到首頁</a>");
            response.addHtmlContents("</table>");
            response.addHtmlContents("</center>");
            response.addHtmlContents("</body>");
            response.addHtmlContents("</html>");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //刪除:--->其實就是處理"/deleteUser"請求
    @RequestMapping("/deleteUser")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在刪除用戶....");
        //從請求路徑中獲取某個參數
        String id = request.getParameter("id");

        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();
        ) {
            //編寫sql語句
            String sql = "DELETE " +
                    "FROM userinfo " +
                    "WHERE id=" + id;

            //創建語句對象，執行sql語句
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            response.sendRedirect("/userList");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
