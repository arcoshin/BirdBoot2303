package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.controller.UserController;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
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
         */
        try {
            //獲取UserController所在包的位置 --- 綁定controller包類對象位置
            File controllerDir = new File(baseDir,"com/birdboot/controller");
            //=============================================>注意此處使用的是"/"

            //遍歷controller包:只獲取字節碼文件
            File[] files = controllerDir.listFiles(f -> f.getName().endsWith(".class"));
            for (File file : files) {
                //獲取字節碼文件名:xxxxx.class
                String fileName = file.getName();
                //修改名稱:xxxxx.class -> xxxxx
                fileName = fileName.substring(0, fileName.indexOf("."));
                //生成類對象
                Class cls = Class.forName("com.birdboot.controller." + fileName);
                //=================================>注意此處使用的是"."

                //判定是否被Controller註解類所標注
                if (cls.isAnnotationPresent(Controller.class)) {//如果是
                    //如果是，那就獲取該類中所有公開方法
                    Method[] methods = cls.getMethods();
                    //遍歷這些方法
                    for (Method method : methods) {
                        //判斷方法是否被RequestMapping註解類所標注
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            //如果是，獲取RequestMapping標注時所傳入的參數值
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();
//                            System.out.println(value);//"/regUser"、"/loginUser"

                            //如果路徑所擷取出來的路徑名與參數所傳入的值相等，則代表是請求一個業務
                            if (value.equals(path)) {
                                //實例化這個對象
                                Object obj = cls.newInstance();
                                //調用對應的業務
                                method.invoke(obj, request, response);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
