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
            char pre = 'a',cur = 'a';
            StringBuilder builder = new StringBuilder();
            while ((d = in.read()) != -1) {
                cur = (char) d;
                if(pre==13&&cur==10){
                    break;
                }
                builder.append(cur);
                pre = cur;

            }
            String line = builder.toString().trim();
            System.out.println("請求行:" + line);

            String[] sentence = line.split("\\s");
            System.out.println("len:" + sentence.length);
            String method = sentence[0];//請求方式
            String uri = sentence[1];//請求方式
            String protocol = sentence[2];//請求方式

            System.out.println("method = " + method);
            System.out.println("uri = " + uri);
            System.out.println("protocol = " + protocol);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
