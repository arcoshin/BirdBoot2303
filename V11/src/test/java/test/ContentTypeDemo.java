package test;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * 響應頭格式測試類
 */
public class ContentTypeDemo {
    public static void main(String[] args) {
        MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();

        File file = new File("./demo.json");
        /**
         * 依照所傳檔案類型，自動去/mime.types查找類型並以字符串返回
         */
        String contentType = mftm.getContentType(file);
        System.out.println(contentType);
    }
}
