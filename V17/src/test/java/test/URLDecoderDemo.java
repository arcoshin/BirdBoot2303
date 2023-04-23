package test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class URLDecoderDemo {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "username=%E8%A8%B1%E5%9F%B9%E5%9F%B9&password=123456&nickname=123&age=123";


        /**
         * 可發現URLDecoder.decode只針對亂數部分正確解碼
         */

        String decode = URLDecoder.decode(str,"UTF-8");
        System.out.println(decode);//username=許培培&password=123456&nickname=123&age=123

    }
}
