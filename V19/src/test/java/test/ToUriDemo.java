package test;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.controller.UserController;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

public class ToUriDemo {
    public static void main(String[] args) throws URISyntaxException {
        try {
            //獲取UserController所在包的位置 --- 綁定controller包類對象位置
            File baseName = new File(
                    UserController.class.getResource(".").toURI()
            );
//        System.out.println("baseName = " + baseName);//...target\classes\com\birdboot\controller

            //獲取UserController所在包名 ---> controller包完整路徑
            String packageName = UserController.class.getPackage().getName();
//        System.out.println(name);//com.birdboot.controller

            //遍歷controller包:只獲取字節碼文件
            File[] files = baseName.listFiles(f -> f.getName().endsWith(".class"));
            for (File file : files) {
                //獲取字節碼文件名:xxxxx.class
                String fileName = file.getName();
                //修改名稱:xxxxx.class -> xxxxx
                fileName = fileName.substring(0,fileName.indexOf("."));
                //生成類對象
                Class cls = Class.forName(packageName + "." + fileName);
                //判定是否被Controller註解類所標注
                if (cls.isAnnotationPresent(Controller.class)){//如果是
                    //如果是，那就獲取該類中所有公開方法
                    Method[] methods = cls.getMethods();
                    //遍歷這些方法
                    for (Method method : methods) {
                        //判斷方法是否被RequestMapping註解類所標注
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            //如果是，獲取RequestMapping標注時所傳入的參數值
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();
//                            System.out.println(value);//regUser、loginUser
                        }
                        continue;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return;
    }
}
