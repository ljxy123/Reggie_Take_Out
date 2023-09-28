package com.lijun.common;
/*
* 封装一个Threadlocal的工具类，用来设置并获取登录者的id
* Threadlocal是在每一个线程当中，保存了相应的值，每一个不同的线程中，值都是不同的，可以用来区分变量
* */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
