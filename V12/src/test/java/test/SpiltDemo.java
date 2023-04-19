package test;

import java.util.Arrays;

/**
 * 字符串重載的split方法
 * string split(string regex,int limit)
 */
public class SpiltDemo {
    public static void main(String[] args) {
        String line = "===1=2==3===4====5=6=7===";

        String[] data = line.split("=");//頭每份"="一切，全尾切去
        System.out.println(Arrays.toString(data));//[, , , 1, 2, , 3, , , 4, , , , 5, 6, 7]

        data = line.split("=",1);//只要求切成一份....就是沒切
        System.out.println(Arrays.toString(data));//[===1=2==3===4====5=6=7===]

        data = line.split("=",2);//只要求切成兩分
        System.out.println(Arrays.toString(data));//[, ==1=2==3===4====5=6=7===]

        data = line.split("=",3);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , =1=2==3===4====5=6=7===]

        data = line.split("=",4);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1=2==3===4====5=6=7===]

        data = line.split("=",5);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1, 2==3===4====5=6=7===]

        data = line.split("=",6);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1, 2, =3===4====5=6=7===]

        data = line.split("=",7);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1, 2, , 3===4====5=6=7===]

        data = line.split("=",8);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1, 2, , 3, ==4====5=6=7===]

        data = line.split("=",9);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1, 2, , 3, , =4====5=6=7===]

        data = line.split("=",10);//只要求切成三份
        System.out.println(Arrays.toString(data));//[, , , 1, 2, , 3, , , 4====5=6=7===]

        data = line.split("=",100);//只要求切成100分，不足則應切盡切
        System.out.println(Arrays.toString(data));//[, , , 1, 2, , 3, , , 4, , , , 5, 6, 7, , , ]

//        data = line.split("=",0);//等同於line.split("=")
//        System.out.println(Arrays.toString(data));//[, , , , , 1, 2, , 3, , , 4, , , , 5, 6, 7]
//
//
//        data = line.split("=",-1);//負數一律應切盡切(頭尾重複時也切)
//        System.out.println(Arrays.toString(data));//[, , , , , 1, 2, , 3, , , 4, , , , 5, 6, 7, , , , , ]
//
//
//        data = line.split("=",-9999);//負數一律應切盡切(頭尾重複時也切)
//        System.out.println(Arrays.toString(data));//[, , , , , 1, 2, , 3, , , 4, , , , 5, 6, 7, , , , , ]


    }
}
