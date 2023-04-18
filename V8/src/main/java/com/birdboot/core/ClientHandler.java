package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * 線程任務類--->該線程用以與用戶端進行HTTP交互
 * <p>
 * 交互有三步 :
 * 1.解析請求
 * 2.處理請求
 * 3.發送響應
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            /**
             * 1.解析請求:
             * V4:將請求的細節移至request構造器中進行
             */

            //通過實例化解析請求
            HttpServletRequest request = new HttpServletRequest(socket);
            /**
             * V7:特別注意，HttpServletResponse的實例化並不是在發送響應時才實例化
             * 因為至少要經過第二步處理請求時，才能知道要響應什麼訊息給客戶端
             * 所以至少要在第二步處理請求開始之前就必須實例化
             * 這樣進行到第二步的Controller時才能調用到HttpServletResponse的實例化並不是在發送響應時才實例化
             * 而到處理完畢時，再來調用響應的方法response
             */
            HttpServletResponse response = new HttpServletResponse(socket);

            /**
             * 2.處理請求(V5先跳過)
             */
            //V8:將處理請求的操作移動到DispatcherServlet中的service方法中進行
            new DispatcherServlet().service(request, response);//調用service方法


            /**
             * 3.發送響應(V5)
             */
            response.response();


        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //V8新增:HTTP協議規定，要求瀏覽器與服務端交互完畢後應該要斷開連接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

