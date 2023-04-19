package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 程序調度員
 * 該類是SpringMVC框架與Tomcat整合時的一個關鍵類
 */
public class DispatcherServlet {
    private static File baseDir;//類加載路徑
    private static File staticDir;//類加載路徑下的static目錄
    static {
        try {
            //定位類加載路徑(V8優化:路徑是固定的，可以改寫為靜態變量，全局獨一份)
            baseDir = new File(
                    DispatcherServlet.class.getClassLoader().getResource(".").toURI()
            );//C:\Users\User\IdeaProjects\BirdBoot2303\V5\target\classes

            //定位類加載目錄下的static目錄(V8優化:路徑是固定的，可以改寫為靜態變量，全局獨一份)
            staticDir = new File(baseDir, "static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * service實際上是繼承了
     */
    public void service(HttpServletRequest request, HttpServletResponse response) {
        //獲取請求的抽象路徑
        String path = request.getUri();
        System.out.println(path);

        //V8改寫，同時執行對象已經改為DispatcherServlet
//        //定位類加載路徑(V8優化:路徑是固定的，可以改寫為靜態變量，全局獨一份
//        File baseDir = new File(
//                ClientHandler.class.getClassLoader().getResource(".").toURI()
//        );//C:\Users\User\IdeaProjects\BirdBoot2303\V5\target\classes
//
//        //定位類加載目錄下的static目錄(V8優化:路徑是固定的，可以改寫為靜態變量，全局獨一份)
//        File staticDirs = new File(baseDir, "static");

        /**
         * 定位要響應的文件(V5的第二個測試目標)
         *
         * V6要討論:
         * static/123.html......定位目標不存在
         * static/..............定位目標為目錄，後面的流一樣會報錯
         * 因此要在此設立分支排除
         */
        File file = new File(staticDir, path);

        if (file.isFile()) {//如果存在並且非目錄
            //因為出現機率較高而讓setStatusCode、setStatusReason皆有默認值，故此處不必特別聲明
            response.setContentFile(file);
            /**
             * V10將response.addHeaders業務一併放入setContent中
             * 於設定響應正文對應的實體對象時，自動生成對應的響應頭
             */
        } else {//否則
            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            response.setContentFile(new File(staticDir, "404.html"));
        }

    }
}
