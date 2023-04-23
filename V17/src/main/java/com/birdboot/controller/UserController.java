package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.*;


/**
 * 處理用戶相關業務
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
