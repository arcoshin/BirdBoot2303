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

            //獲取請求的抽象路徑
            String path = request.getUri();
            System.out.println(path);

            //定位類加載路徑
            File baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );//C:\Users\User\IdeaProjects\BirdBoot2303\V5\target\classes

            //定位類加載目錄下的static目錄
            File staticDirs = new File(baseDir, "static");

            /**
             * 定位要響應的文件(V5的第二個測試目標)
             *
             * V6要討論:
             * static/123.html......定位目標不存在
             * static/..............定位目標為目錄，後面的流一樣會報錯
             * 因此要在此設立分支排除
             */
            File file = new File(staticDirs, path);

            if (file.isFile()) {//如果存在並且非目錄
                //因為出現機率較高而讓setStatusCode、setStatusReason皆有默認值，故此處不必特別聲明
                response.setContentFile(file);
            } else {//否則
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setContentFile(new File(staticDirs, "404.html"));
            }


            /**
             * 3.發送響應(V5的第一個測試目標 : 將static下的index.html發送給瀏覽器)
             */
            response.response();




        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


}

