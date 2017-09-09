package com.jessie.aspectjdemo;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author JessieKate
 * @date 09/09/2017
 * @email lyj1246505807@gmail.com
 * @describe 自定义切点用法切面
 */
@Aspect
public class MyPointcutsAspect {
    private static final String MY_POINTCUTS_TAG = "自定义切点用法测试";

    //在我们自定义切点注释的方法处插入
    @Pointcut("execution(@com.jessie.aspectjdemo.MyPointcuts * *(..))")
    public void myPointcutsMethod() {
    }

    //在这些方法的前后插入
    @Around("myPointcutsMethod()")
    public void onMyPointcutsMethodAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String key = proceedingJoinPoint.getSignature().toString();
        Log.d(MY_POINTCUTS_TAG, "方法调用之前插入: " + key);
        proceedingJoinPoint.proceed();
        Log.d(MY_POINTCUTS_TAG, "方法调用之后插入: " + key);
    }

}
