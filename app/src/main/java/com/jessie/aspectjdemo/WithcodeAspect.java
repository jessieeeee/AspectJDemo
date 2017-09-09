package com.jessie.aspectjdemo;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author JessieKate
 * @date 09/09/2017
 * @email lyj1246505807@gmail.com
 * @describe 精确插入代码用法切面
 */

@Aspect
public class WithcodeAspect {

    private static final String WITH_CODE = "精确插入测试";


    // 在testWithcode2()方法内插入
    @Pointcut("withincode(* com.jessie.aspectjdemo.MainActivity.testWithcode2(..))")
    public void testWithcode2Method() {
    }

    // 调用testComment()方法的时候插入
    @Pointcut("call(* com.jessie.aspectjdemo.MainActivity.testComment(..))")
    public void testCommentMethod() {
    }

    // 同时满足以上条件，在testWithcode2()方法内调用testComment()方法的时候才插入
    @Pointcut("testWithcode2Method() && testCommentMethod()")
    public void testWithcode2MethodOnlyInvoke() {
    }

    //在这些方法的后面插入
    @After("testWithcode2MethodOnlyInvoke()")
    public void beforeWithcode2MethodOnlyInvoke(JoinPoint joinPoint) {
        String key = joinPoint.getSignature().toString();
        Log.d(WITH_CODE, "beforeWithcode2MethodOnlyInvoke: " + key);
    }

}
