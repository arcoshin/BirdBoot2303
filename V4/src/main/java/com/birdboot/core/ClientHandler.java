package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
             * 通過實例化解析請求
             */

            //V4:將請求的細節移至request構造器中進行
            HttpServletRequest request = new HttpServletRequest(socket);
            //獲取請求的抽象路徑
            String path = request.getUri();
            System.out.println(path);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

