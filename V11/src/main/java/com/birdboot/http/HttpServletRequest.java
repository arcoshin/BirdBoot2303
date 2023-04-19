package com.birdboot.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    private Socket socket;

    private String method;//請求方式
    private String uri;//抽象路徑
    private String protocol;//協議版本
    private Map<String, String> headers = new HashMap<>();//鍵值對:(消息頭=消息頭對應的值)

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;

        /**
         * 解析請求行
         */
        parseRequestLine();

        /**
         * 解析消息頭
         */
        parseHeaders();

        /**
         * 解析消息正文
         */
        parseContent();

    }

    /**
     * 解析請求行的方法
     */
    private void parseRequestLine() throws IOException, EmptyRequestException {
        //獲得請求行
        String line = readline();

        /**
         * V11新增:服務器應該忽略來自客戶端的空請求
         * 倘若接收到空請求時應該拋異常
         * 當ClientHandler利用異常捕獲機制接收到此異常時，程序會直接跳到catch分支
         * 此時catch中什麼動作也不執行(依照HTTP協議要求自動忽略)
         * 待catch塊執行結束後時，程序會自動跳轉至finally塊(異常捕獲機制)，此時塊中會關閉socket流
         * 藉此流程完成HTTP協議要求--->服務器應該忽略來自客戶端的空請求
         */
        if (line.isEmpty()){//原則上第一行不可能為空，因此isEmpty可以有效分辨是否為空請求
            throw new EmptyRequestException();
        }

        //解析請求行中的相關訊息(以空白分割)
        String[] sentence = line.split("\\s");

        //依照分段賦值相關訊息
        try {
            method = sentence[0];//請求方式
            uri = sentence[1];//請求方式
            protocol = sentence[2];//請求方式
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("method = " + method);//打樁
        System.out.println("uri = " + uri);//打樁
        System.out.println("protocol = " + protocol);//打樁
    }

    /**
     * 解析消息頭的方法
     */
    private void parseHeaders() throws IOException {
        //獲取消息行
        while (true) {
            //讀取每一行
            String line = readline();

            if (line.isEmpty()) {//如果讀取空行
                break;//代表結尾了
            }//否則繼續迴圈

            //依照": "作為拆分的依據
            String[] split = line.split(":\\s");

            //存入headers
            String key = split[0];
            String value = split[1];
            headers.put(key, value);

        }//循環結束
        headers.forEach((k, v) -> System.out.println("消息頭:" + k + ",消息正文:" + v));
    }

    /**
     * 解析消息正文的方法
     */
    private void parseContent() {

    }

    /**
     * 封裝方法--->讀一行的方法
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


    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
