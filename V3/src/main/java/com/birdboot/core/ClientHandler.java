package com.birdboot.core;

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
        String line = null;
        try {
            /**
             * 解析請求
             */

            //獲得請求行
            line = readline();
//            System.out.println("請求行:" + line);//打樁

            //解析請求行中的相關訊息(以空白分割)
            String[] sentence = line.split("\\s");
//            System.out.println("len:" + sentence.length);//打樁

            //依照分段賦值相關訊息
            String method = sentence[0];//請求方式
            String uri = sentence[1];//請求方式
            String protocol = sentence[2];//請求方式
            System.out.println("method = " + method);//打樁
            System.out.println("uri = " + uri);//打樁
            System.out.println("protocol = " + protocol);//打樁


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


        /**
         * 解析消息頭
         */
        Map<String, String> map = new HashMap<>();
        try {//再繼續讀，不知道有多少消息頭，所以用while
            while (true) {
                //讀取每一行
                line = readline();

                if (line.isEmpty()) {//如果讀取空行
                    break;//代表結尾了
                }//否則繼續迴圈

                //依照": "作為拆分的依據
                String[] split = line.split(":\\s");

                //存入map
                String key = split[0];
                String value = split[1];
                map.put(key, value);


            }//循環結束
            System.out.println(map);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 封裝方法--->讀一行
     * 通過socket獲取的輸入流讀取客戶端發送過來的消息
     * 共用方法處理異常，應拋出異常好讓調用者各自檢查解決
     */
    private String readline() throws IOException {
        InputStream in = socket.getInputStream();

        int d;
        char pre = 'a', cur = 'a';
        StringBuilder builder = new StringBuilder();
        while ((d = in.read()) != -1) {
            cur = (char) d;
            if (pre == 13 && cur == 10) {
                break;
            }
            builder.append(cur);
            pre = cur;

        }
        return builder.toString().trim();
    }
}

