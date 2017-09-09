package com.jessie.aspectjdemo;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author JessieKate
 * @date 08/09/2017
 * @email lyj1246505807@gmail.com
 * @describe 基本用法切面
 */

@Aspect
public class BaseAspect {

    private static final String TAG = "基础用法测试";

    //testBeforeAndAfter前插入
    @Before("execution(* com.jessie.aspectjdemo.MainActivity.testBeforeAndAfter(..))")
    public void activityOnMethodBefore(JoinPoint joinPoint){
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "activityOnMethodBefore: 调用方法路径" + key);
    }

    //testBeforeAndAfter后插入
    @After("execution(* com.jessie.aspectjdemo.MainActivity.testBeforeAndAfter(..))")
    public void activityOnMethodAfter(JoinPoint joinPoint){
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "activityOnMethodAfter: 调用方法路径" + key);

    }

    //在testBeforeAndAfter前后插入
    @Around("execution(* com.jessie.aspectjdemo.MainActivity.testAround(..))")
    public void activityOnMethodAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String key = proceedingJoinPoint.getSignature().toString();

        Log.d(TAG,"tivityOnMethodAroundFirst:在" + key + "方法之前调用 ");
        proceedingJoinPoint.proceed();
        Log.d(TAG, "activityOnMethodAroundSecond:在" + key+"方法之后调用 ");
    }

}
