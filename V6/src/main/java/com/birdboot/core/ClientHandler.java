package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;

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

            int statusCode;//狀態代碼
            String statusReason;//狀態描述

            if (file.isFile()) {//如果存在並且非目錄
                statusCode = 200;
                statusReason = "OK";
            } else {//否則
                statusCode = 404;
                statusReason = "NotFound";
                file = new File(staticDirs, "404.html");
            }


            /**
             * 3.發送響應(V5的第一個測試目標 : 將static下的index.html發送給瀏覽器)
             */

//                一則響應的大致格式
//                HTTP/1.1 200 OK(CRLF) <---狀態行
//                Content-Type: text/html(CRLF) <---響應頭
//                Content-Length: 2546(CRLF)(CRLF) <---響應頭
//                1011101010101010101......(index.html页面内容) <---響應正文

            //建立發送響應的流
            BufferedOutputStream bos = new BufferedOutputStream(
                    socket.getOutputStream()
            );

            //3-1發送狀態行
            println("HTTP/1.1" + " " + statusCode + " " + statusReason);

            //3-2發送響應頭
            println("Content-Type: text/html");//響應正文格式
            println("Content-Length: " + file.length());//響應正文長度
            println("");//響應頭發送完畢要單獨發送一行迴車換行符，發送空字符串即迴車換行

            //3-3發送響應正文
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file)
            );

            int d;
            while ((d = bis.read()) != -1) {
                bos.write(d);
            }
            bos.close();//清空緩存


        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * 向客戶發送一行字符串的方法
     *
     * @param line 發送的內容
     */
    private void println(String line) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(
                socket.getOutputStream()
        );
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        bos.write(data);
        bos.write(13);//發送迴車符CR
        bos.write(10);//發送換行符LF
        bos.flush();
    }

}

