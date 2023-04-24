package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.controller.UserController;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * service
     */
    public void service(HttpServletRequest request, HttpServletResponse response) {
        //獲取請求的抽象路徑
        String path = request.getRequestURI();
        System.out.println("請求路徑 = " + path);

        /**
         * V13要開始先判斷請求路徑是否為所列業務:
         * V17使用註解優化這段代碼
         * V18將"其中的掃描業務"移往HandlerMapping單獨處理
         */
        Method method = HandlerMapping.getMethod(path);
        if (method != null) {//代表為請求業務
            //通過方法獲取其所屬類的類對象
            try {
                Class cls = method.getDeclaringClass();
                Object obj = cls.newInstance();
                method.invoke(obj, request, response);
            } catch (Exception e) {
                //若程序在這段代碼中報錯，那就是典型的音為服務端處理請求出現錯誤而導致處理失敗(500錯誤)
                response.setStatusCode(500);
                response.setStatusReason("Internal Server Error");
                //瀏覽器有默認的500及404錯誤
            }

        } else {//否則為抽象路徑
            /**
             * 定位要響應的文件及404分支
             */
            File file = new File(staticDir, path);

            if (file.isFile()) {//如果存在並且非目錄
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
}
