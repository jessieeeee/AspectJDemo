package com.jessie.aspectjdemo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author JessieKate
 * @date 09/09/2017
 * @email lyj1246505807@gmail.com
 * @describe 自定义的Pointcuts，可插入多个切点
 */
@Retention(RetentionPolicy.CLASS) //声明注解类
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD}) //作用于构造方法和方法
public @interface MyPointcuts {

}
