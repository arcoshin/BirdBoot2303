package com.birdboot.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpServletRequest {
    private Socket socket;

    private String method;//請求方式
    private String uri;//抽象路徑
    private String protocol;//協議版本

    private String requestURI;//保存uri中"?"左側的請求路徑
    private String queryString;//保存uri中"?"右側的參數部分
    private Map<String, String> parameters = new HashMap<>();//保存uri中參數部分解析結果的鍵值對

    private Map<String, String> headers = new HashMap<>();//消息頭鍵值對:(消息頭=消息頭對應的值)

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
         * 解析消息正文:V15表單使用post提交，其數據就不會顯示在uri中
         * 此時就要開始解析正文，判定條件為請求中的消息頭------>Content Length != 0
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
        if (line.isEmpty()) {//原則上第一行不可能為空，因此isEmpty可以有效分辨是否為空請求
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

        //由於uri可能帶參數，因此需要進一步二次解析
        parseURI();

        System.out.println("requestURI = " + requestURI);//打樁
        System.out.println("queryString = " + queryString);//打樁
        System.out.println("parameters = " + parameters);//打樁


    }

    /**
     * 進一步解析uri的方法
     */
    private void parseURI() {
        /**
         * uri的參數情形有兩種
         * 1.不含參數的
         * 例如:  /index.html
         *
         * 2.含參數的
         * 例如:  /regUser?username=xsupeipei&password=123456&nickname=peipei&age=999
         *
         * 3.輸入框未命名時的提交
         * 例如:  /regUser?
         *
         * 判定uri型態             以"?"切割
         * /index.html       data:[/index.html]
         * /regUser?xxxxx    data:[/regUser,xxxxx]
         * /regUser?         data:[/regUser]
         */
        String[] split = uri.split("\\?");//依照"?"做分割
        requestURI = split[0];//"?"左側為請求路徑:/regUser

        if (split.length > 1) {
            queryString = split[1];//"?"右側為參數團:username=xsupeipei&password=123456&nickname=peipei&age=999

            /**
             * 進一步解析參數團queryString
             * (username=xsupeipei&password=123456&nickname=peipei&age=999)
             */
            parseParameters(queryString);
        }
    }

    /**
     * 解析參數
     */
    private void parseParameters(String line) {
        String[] entry = line.split("&");//依照"&"符號作為分割依據，得到數個鍵值對
        for (String s : entry) {//遍歷這個鍵值對

            //每份鍵值對再以"="切割
            String[] e = s.split("=", 2);
            parameters.put(e[0], e[1]);//split重載方法

        }
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
        headers.forEach((k, v) -> System.out.println("消息頭:" + k + "," + v));
    }

    /**
     * 解析消息正文的方法
     */
    private void parseContent() throws IOException {
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(getHeader("Content-Length"));//獲取文件長度
            byte[] contentData = new byte[contentLength];//一次塊讀整份文件
            System.out.println("====================正文長度" + contentLength);
            InputStream in = socket.getInputStream();
            in.read(contentData, 0, contentData.length);//一次塊讀全部


            String contentType = headers.get("Content-Type");
            if ("application/x-www-form-urlencoded".equals(contentType)) {
                //正文是form表單提交的數據(原get提交請求是在抽象路徑"?"右側部分)

                /**
                 * 如何把字節碼數組解析為字符串?
                 * 使用new String(data[],解碼格式)
                 */
                String line = new String(contentData, StandardCharsets.ISO_8859_1);
                System.out.println("====================正文" + line);
                parseParameters(line);
            }
            //後續可擴支持其他正文類型的解析


        }
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

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
