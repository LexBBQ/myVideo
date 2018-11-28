package com.half.utils;

import java.util.Random;

/**
 * 生成随机id
 * 时间戳+3位随机数
 */
public class IdUtils {
    public static synchronized   String getId(){
        long t = System.currentTimeMillis();
        Random r=new Random(10);
        int i = r.nextInt()+10;
        return String.valueOf(t+i);
    }
}
