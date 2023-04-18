package com.birdboot.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServletResponse {
    private Socket socket;

    //狀態行相關訊息(而因出現機率較高而默認讓setStatusCode=200、setStatusReason="OK")
    private int statusCode = 200;//狀態代碼，默認200
    private String statusReason = "OK";//狀態描述，默認OK

    //響應頭相關訊息
    //V7暫時不討論

    //響應正文相關訊息
    private File contentFile;//響應正文對應的實體文件

    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }

    /**
     * 用標準的HTTP響應格式發給客戶端的方法
     */
    public void response() throws IOException {

        //3-1發送狀態行
        sendStatusLine();

        //3-2發送響應頭
        sendHeaders();

        //3-3發送響應正文
        sendContent();

    }

    /**
     * 發送狀態行的方法
     */
    private void sendStatusLine() throws IOException {
        println("HTTP/1.1" + " " + statusCode + " " + statusReason);
    }

    /**
     * 發送響應頭的方法
     */
    private void sendHeaders() throws IOException {
        println("Content-Type: text/html");//響應正文格式
        println("Content-Length: " + contentFile.length());//響應正文長度
        println("");//響應頭發送完畢要單獨發送一行迴車換行符，發送空字符串即迴車換行
    }

    /**
     * 發送響應正文的方法
     */
    private void sendContent() throws IOException {
        //輸入流
        BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(contentFile)
        );

        //輸出流
        BufferedOutputStream bos = new BufferedOutputStream(
                socket.getOutputStream()
        );

        int d;
        while ((d = bis.read()) != -1) {
            bos.write(d);
        }
        bos.close();//flush清空緩存，同時關閉流
    }

    /**
     * 向客戶發送一行字符串的方法
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
    }
}
