package com.birdboot.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 主啟動類
 */
public class BirdBootApplication {
    private ServerSocket serverSocket;

    public BirdBootApplication() {
        try {
            System.out.println("正在啟動服務端......");
            serverSocket =new ServerSocket(8088);
            System.out.println("服務端已啟動完畢!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            System.out.println("正在等待用戶端連接......");
            Socket socket = serverSocket.accept();
            System.out.println("一個用戶端已連接!!!");

            /**
             * 利用構造器將socket傳入"與客戶進行HTTP交互"的線程任務類並啟動
             */
            new Thread(new ClientHandler(socket)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        BirdBootApplication birdBootApplication = new BirdBootApplication();
        birdBootApplication.start();
    }
}
