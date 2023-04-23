package com.birdboot.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主啟動類
 */
public class BirdBootApplication {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;//線程池

    public BirdBootApplication() {
        try {
            System.out.println("正在啟動服務端......");
            serverSocket = new ServerSocket(8088);
            System.out.println("服務端已啟動完畢!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {//V5時，一問一答模式已完成，即可開啟死循環以不斷接收用戶端的請求
        try {
            while (true) {
                System.out.println("正在等待用戶端連接......");
                Socket socket = serverSocket.accept();
                threadPool = Executors.newFixedThreadPool(50);
                System.out.println("一個用戶端已連接!!!");

                /**
                 * 利用構造器將socket傳入"與客戶進行HTTP交互"的線程任務類並啟動
                 */
//                new Thread(new ClientHandler(socket)).start();
                threadPool.execute(new ClientHandler(socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new BirdBootApplication().start();
    }
}
