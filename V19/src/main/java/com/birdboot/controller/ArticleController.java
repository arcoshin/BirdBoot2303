package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.Article;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.*;
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
    @RequestMapping("articleWriter")
    public void articleWrite(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在發布新文章");
        /**
         * 取得表單內容
         */
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");

        /**
         * 簡易驗證
         */
        if (
                title == null || title.trim().isEmpty() ||
                        author == null || author.trim().isEmpty() ||
                        content == null || content.trim().isEmpty()
        ) {
            try {
                response.sendRedirect("article_info_error.html");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 存入本地
         */
        Article article = new Article(title, author, content);

        /**
         * 建立流，綁定文件
         */
        File file = new File(articles, title + ".txt");
        System.out.println(file.getName());
        //重複性驗證
        if (file.exists()) {
            try {
                response.sendRedirect("article_info_already_exist.html");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //建立對象輸出流
        try (
                ObjectOutputStream oos = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );
        ) {
            oos.writeObject(article);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 發送響應
         */
        try {
            response.sendRedirect("article_success.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//
//    /**
//     * 文章列表
//     */
//    @RequestMapping("articleList")
//    public void selectArticle(HttpServletRequest request, HttpServletResponse response) {
//        System.out.println("正在生成文章列表......");
//
//        /**
//         * 建立一文章集合，以利後續動態生成列表用
//         */
//        List<Article> list = new ArrayList<>();
//
//        /**
//         * 遍歷文章存放目錄中所有文件
//         */
//        File[] subs = articles.listFiles(f -> f.getName().endsWith(".txt"));
//
//        /**
//         * 遍歷所得結果，並將其拼接綁定至對象輸入流
//         */
//        for (File sub : subs) {
//            //拼接位置
//            File file = new File(articles, sub.getName());
//
//            //建立對象輸入流
//            try (
//                    ObjectInputStream ois = new ObjectInputStream(
//                            new BufferedInputStream(
//                                    new FileInputStream(file)
//                            )
//                    );
//            ) {
//                /**
//                 * 將讀入結果傳入集合
//                 */
//                Article article = (Article) ois.readObject();
//                list.add(article);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * 發送響應:動態生成頁面
//         */
//        try {
//            response.sendContentType("text/html;charset=utf-8");
//            PrintWriter pw = response.getWriter();
//
//            pw.println("<!DOCTYPE html>");
//            pw.println("<html lang=\"en\">");
//            pw.println("<head>");
//            pw.println("<meta charset=\"UTF-8\">");
//            pw.println("<title><b>用戶列表</b></title>");
//            pw.println("</head>");
//
//            pw.println("<body>");
//            pw.println("<center>");
//            pw.println("<table border=\"1\" align=\"center\">");
//
//            pw.println("<tr>");
//            pw.println("<td><b>標題</b></td>");
//            pw.println("<td><b>作者</b></td>");
//            pw.println("</tr>");
//
//            for (Article article : list) {
//                pw.println("<tr>");
//                pw.println("<td><b>" + article.getTitle() + "</b></td>");
//                pw.println("<td><b>" + article.getAuthor() + "</b></td>");
//                pw.println("</tr>");
//            }
//
//            pw.println("</table>");
//            pw.println("<a href=\"/index.html\">點我回首頁</a><br>");
//            pw.println("</center>");
//            pw.println("</body>");
//            pw.println("</html>");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}