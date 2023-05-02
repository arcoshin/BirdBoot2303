package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.Article;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;
import com.birdboot.util.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ArticleController用於處理所有與文章有關的請求
 */

@Controller //註記為控制器類
public class ArticleController {
    private static File articles;

    static {
        articles = new File("articles");
        if (!articles.exists()) {
            articles.mkdirs();
        }
    }

    /**
     * 發布文章
     */
    @RequestMapping("/articleWriter")
    public void Write(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在發布新文章......");
        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection()
        ) {
            System.out.println("正在發布新文章");
            /**
             * 取得表單內容
             */
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String content = request.getParameter("content");

            /**
             * 非空驗證
             */

            if (
                    title == null || title.isEmpty() ||
                            author == null || author.isEmpty() ||
                            content == null || content.isEmpty()
            ) {
                //驗證為null或為空
                response.sendRedirect("article_info_error.html");
                return;
            }

            /**
             * 重複性驗證
             */
            //編寫sql語句 : 查找同名文章
            String checkSQL = "SELECT id,title,author,content " +
                    "FROM articleinfo " +
                    "WHERE title = ?";

            //預編譯
            PreparedStatement ps1 = connection.prepareStatement(checkSQL);

            //聲明參數
            ps1.setString(1, title);

            //執行語句
            ResultSet resultSet = ps1.executeQuery();

            //驗證結果集
            if (resultSet.next()) {//如果查找同名文章有結果
                response.sendRedirect("article_info_already_exist.html");

            } else {//如果沒有結果--->通過重複性驗證
                /**
                 * 寫入數據庫
                 */
                //編寫sql語句 : 插入文章
                String sql = "INSERT INTO articleinfo (title,author,content) " +
                        " VALUES (?,?,?) ";

                //預編譯
                PreparedStatement ps2 = connection.prepareStatement(sql);

                //聲明參數
                ps2.setString(1, title);
                ps2.setString(2, author);
                ps2.setString(3, content);

                //執行語句
                int num = ps2.executeUpdate();

                //驗正
                if (num > 0) {
                    System.out.println("文章新增完成");
                    response.sendRedirect("/article_success.html");
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 文章列表
     */
    @RequestMapping("/articleList")
    public void articleList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("開始生成文章列表");

        /**
         * 連接數據庫
         */
        //讀取資料庫
        try (Connection connection = DBUtil.getConnection()) {
            String checkSQL = "SELECT id,title,author,content " +
                    "FROM articleinfo ";
            ResultSet resultSet = connection.createStatement().executeQuery(checkSQL);

            /**
             * 寫入HTML頁面
             */
            response.addHeaders("Content-Type", "text/html;charset=utf-8");

            /**
             * 正文開始
             */
            response.addHtmlContents("<!DOCTYPE html>");
            response.addHtmlContents("<html lang=\"en\">");
            response.addHtmlContents("<head>");
            response.addHtmlContents("<meta charset=\"UTF-8\">");
            response.addHtmlContents("<title>文章列表</title>");
            response.addHtmlContents("</head>");
            response.addHtmlContents("<body>");
            response.addHtmlContents("<center>");
            response.addHtmlContents("<h1>文章列表</h1>");
            response.addHtmlContents("<table border=\"1\">");
            response.addHtmlContents("<tr>");
            response.addHtmlContents("<td>ID</td>");
            response.addHtmlContents("<td>文章標題</td>");
            response.addHtmlContents("<td>文章作者</td>");
            response.addHtmlContents("<td>文章內容</td>");
            response.addHtmlContents("<td>權限操作</td>");
            response.addHtmlContents("</tr>");


            //分析結果集
            while (resultSet.next()) {//將指針移到下一行數據，如果存在......

                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String content = resultSet.getString("content");


                response.addHtmlContents("<tr>");
                response.addHtmlContents("<td>" + id + "</td>");
                response.addHtmlContents("<td>" + title + "</td>");
                response.addHtmlContents("<td>" + author + "</td>");
                response.addHtmlContents("<td>" + content + "</td>");
                response.addHtmlContents("<td><a href=\"/deleteArticle?id=" + id + "\">刪除</a></td>");
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

        }
    }

    /**
     * 刪除文章
     */
    @RequestMapping("/deleteArticle")
    public void deleteArticle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在刪除文章......");
        //從請求路徑中獲取某個參數
        String id = request.getParameter("id");

        try (
                //連接數據庫
                Connection connection = DBUtil.getConnection();
        ) {
            //編寫sql語句
            String sql = "DELETE " +
                    "FROM articleinfo " +
                    "WHERE id=" + id;

            //創建語句對象，執行sql語句
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            response.sendRedirect("/articleList");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}