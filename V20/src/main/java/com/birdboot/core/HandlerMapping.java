package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * SpringMVC使用這個框架來維護所有Controller
 */
public class HandlerMapping {
    private static File baseDir;//類加載路徑


    /**
     * key:請求路徑
     * value:處理請求的業務方法
     */
    private static Map<String, Method> mapping = new HashMap<>();

    static {
        try {
            //初始化類加載對象地址
            baseDir = new File(
                    //V18:定位應該使用自己的類對象地址
                    HandlerMapping.class.getClassLoader().getResource(".").toURI()
            );

            //初始化mapping
            initMapping();
        } catch (URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化mapping的方法
     */
    private static void initMapping() throws ClassNotFoundException {
        //獲取UserController所在包的位置 --- 綁定controller包類對象位置
        File controllerDir = new File(baseDir, "com/birdboot/controller");
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
                        //將以其處理的請求路徑與key&value形式存入mapping
                        mapping.put(value, method);

                    }
                }
            }
        }
    }

    /**
     * 根據請求路徑獲取某個控制器中的對應業務的方法
     */
    public static Method getMethod(String path) {
        return mapping.get(path);
    }

//    public static void main(String[] args) {
//        Method method1 = getMethod("/regUser");
//        System.out.println(method1);
//        //public void com.birdboot.controller.UserController.reg(com.birdboot.http.HttpServletRequest,com.birdboot.http.HttpServletResponse)
//
//        Method method2 = getMethod("/regUser");
//        System.out.println(method2);
//        //public void com.birdboot.controller.UserController.reg(com.birdboot.http.HttpServletRequest,com.birdboot.http.HttpServletResponse)
//
//
//    }
}
