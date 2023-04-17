package com.tedu.springboot2303.controller;

import com.tedu.springboot2303.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserController用於處理所有與用戶有關的請求
 */
@Controller//SpringBoot框架中必須使用的註解，用以識別屬於控制器
public class UserController {
    private static File userDir;//用以存放所有用戶訊息的目錄

    /**
     * 靜態代碼塊只執行一次，節省資源
     */
    static {
        userDir = new File("users");
        if (!userDir.exists()) {//目錄不存在時才創建
            userDir.mkdirs();
        }
    }

    /**
     * 註冊
     */
    @RequestMapping("regUser")//SpringBoot框架中必須使用的註解，用以識別"調用被註記方法的條件"
    public void reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在處理用戶註冊業務......");
        /**
         * 獲取瀏覽器表單中提交的數據
         */
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageString = request.getParameter("age");

        System.out.println("name = " + username + ",pwd = " + password + ",nick = " + nickname + ",age = " + ageString);

        /**
         * 必要簡易驗證
         * 注意，null與isEmpty順序不可互換
         * 否則當isEmpty在前，而所傳入的值為null，此時會報空指針異常
         * 而邏輯或"||"必須是看到true才會繼續判定下一個條件，此時就會讓程序產生錯誤
         */
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                nickname == null || nickname.trim().isEmpty() ||
                ageString == null || ageString.trim().isEmpty() ||
                !ageString.matches("[0-9]+")
        ) {
            try {
                response.sendRedirect("reg_info_error.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        /**
         * 保存這段數據
         */
        int age = Integer.parseInt(ageString);//須排除null再來轉類型，否則有可能空指針
        User user = new User(username, password, nickname, age);

        /**
         * 綁定地址，利用File另外一個構造器表達位址
         * new File(File file1,StringFile2name)
         * 可以綁定file1所在相同目錄中，名為file2的文件
         */
        File file = new File(userDir, username + ".obj");
        /**
         * 重複資料驗證
         */
        System.out.println(file.getName());
        if (file.exists()) {//文件名已存在代表重複註冊
            try {
                response.sendRedirect("reg_info_already_exist.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (
                ObjectOutputStream oos = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );

        ) {
            oos.writeObject(user);

            /**
             * 發送一個結果給用戶(sendRedirect:重定向)
             */
            response.sendRedirect("reg_success.html");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登入
     */
    @RequestMapping("loginUser")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在處理用戶登入業務......");
        /**
         * 獲取登入數據
         */
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("name = " + username + ",pwd = " + password);

        /**
         * 必要簡易驗證 : 其實可以只驗證密碼
         */
        if (
                password == null || password.trim().isEmpty()
        ) {
            try {
                response.sendRedirect("login_info_error.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * 綁定地址，利用File另外一個構造器表達位址
         * new File(File file1,StringFile2name)
         * 可以綁定file1所在相同目錄中，名為file2的文件
         */
        File file = new File(userDir, username + ".obj");
        /**
         * 資料存在驗證
         */
        if (!file.exists()) {//文件名不存在代表輸入錯誤或尚未註冊
            try {
                response.sendRedirect("login_info_not_exist.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 建立對象流讀取用戶資料
         */
        try (
                ObjectInputStream ois = new ObjectInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)
                        )
                );
        ) {

            User o = (User) (ois.readObject());

            /**
             * 比對資料並發送響應
             */
            if (o.getPassword().equals(password)) {

                //比對成功後的響應
                response.sendRedirect("login_success.html");
            } else {

                //比對失敗後的響應
                response.sendRedirect("login_info_not_match.html");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 展示員工列表
     */
    @RequestMapping("userList")
    public void userList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在生成用戶列表......");
        /**
         * 讀取所有已存取的對象資料
         */
        //創立一個集合，用以蒐集動態生成頁面時使用
        List<User> list = new ArrayList<>();

        //將userDir目錄中的所有使用者數據列表讀取(只獲取以".obj"為結尾的文件)
        File[] subs = userDir.listFiles(f -> f.getName().endsWith(".obj"));

        for (File sub : subs) {//遍歷這個輸出的結果
//            System.out.println(sub.getName());//打樁語句

            //拼接完整路徑
            File userFile = new File(userDir, sub.getName());
            try (
                    ObjectInputStream ois = new ObjectInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(userFile)
                            )
                    );
            ) {
                //接收讀取結果
                User user = (User) ois.readObject();
                //將數據傳入集合
                list.add(user);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 發送響應:動態生成頁面
         */
        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title><b>用戶列表</b></title>");
            pw.println("</head>");

            pw.println("<body>");
            pw.println("<center>");
            pw.println("<table border=\"1\" align=\"center\">");

            pw.println("<tr>");
            pw.println("<td><b>戶名</b></td>");
            pw.println("<td><b>密碼</b></td>");
            pw.println("<td><b>暱稱</b></td>");
            pw.println("<td><b>年齡</b></td>");
            pw.println("</tr>");

            for (User user : list) {
                pw.println("<tr>");
                pw.println("<td><b>" + user.getUsername() + "</b></td>");
                pw.println("<td><b>" + user.getPassword() + "</b></td>");
                pw.println("<td><b>" + user.getNickname() + "</b></td>");
                pw.println("<td><b>" + user.getAge() + "</b></td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
            pw.println("<a href=\"/index.html\">點我回首頁</a><br>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
