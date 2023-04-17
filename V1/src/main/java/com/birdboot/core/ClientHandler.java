package com.birdboot.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
        try (
                InputStream in = socket.getInputStream();
        ) {
            int d;
            while ((d = in.read()) != -1) {
                System.out.print((char) d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
