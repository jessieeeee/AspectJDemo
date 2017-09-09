package com.jessie.aspectjdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final String BASE_TAG = "基础用法测试";

    private static final String MY_POINTCUTS_TAG = "自定义切点用法测试";

    private static final String WITH_CODE = "精确插入测试";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBeforeAndAfter();
        testAround();
    }


    public void testWithcode1(View view){
        testComment();
    }

    public void testWithcode2(View view){
        testComment();
    }

    private void testComment(){
        Log.d(WITH_CODE, "testComment: 公共方法执行的代码");
    }

    @MyPointcuts
    public void testMyPointcuts1(View view){
        Log.d(MY_POINTCUTS_TAG, "testMyPointcuts: 测试自定义切点1");
    }

    @MyPointcuts
    public void testMyPointcuts2(View view){
        Log.d(MY_POINTCUTS_TAG, "testMyPointcuts: 测试自定义切点2");
    }

    @MyPointcuts
    public void testMyPointcuts3(View view){
        Log.d(MY_POINTCUTS_TAG, "testMyPointcuts: 测试自定义切点3");
    }

    private void testBeforeAndAfter(){
        Log.d(BASE_TAG, "testBeforeAndAfter: 原本需执行的代码");
    }

    private void testAround(){
        Log.d(BASE_TAG, "testAround: 原本需执行的代码");
    }
}
