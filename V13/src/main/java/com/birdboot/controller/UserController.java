package com.birdboot.controller;

import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.*;
import java.net.URISyntaxException;


/**
 * 處理用戶相關業務
 */
public class UserController {
    private static File userDir;

    static {
        userDir = new File("users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

    //註冊:處理/regUser請求
    public void reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("開始處理用戶註冊......");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username + "," + password + "," + nickname + "," + ageStr);

        /**
         * 必要驗正
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
//                File baseDir = new File(UserController.class.getClassLoader().getResource(".").toURI());
//                File staticDir = new File(baseDir, "static");
//                File file = new File(staticDir, "reg_info_error.html");
//                response.setContentFile(file);
            response.sendRedirect("reg_info_error.html");
            return;
        }

        int age = Integer.parseInt(ageStr);

        /**
         * 建立用戶對象
         */
        User user = new User(username, password, nickname, age);

        /**
         * 綁定用戶對象儲存地址
         */
        File file = new File(userDir, username + ".obj");
        if (file.exists()) {
            response.sendRedirect("reg_info_already_exist.html");
            return;
        }

        /**
         * 建立對象輸出流
         */
        try (
                ObjectOutputStream oos =
                        new ObjectOutputStream(
                                new BufferedOutputStream(
                                        new FileOutputStream(file)
                                )
                        )
        ) {
            /**
             * 寫出對象
             */
            oos.writeObject(user);
            oos.flush();

            /**
             * 成功後應響應給用戶:重定向
             */
            response.sendRedirect("reg_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
