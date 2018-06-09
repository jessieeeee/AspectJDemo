---

# Android AOP 学习入门

---

目前android上应用的比较多的AOP框架是AspectJ

#  android studio 集成

在项目根目录的build.gradle中增加依赖：

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        ...
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:1.0.8'
    }
}
```

在主项目的build.gradle中增加AspectJ的依赖，并添加模块：

```
compile 'org.aspectj:aspectjrt:1.8.9'
```

```
apply plugin: 'android-aspectjx'
```

# 踩坑提醒

在编译的时候，遇到了一个坑，`xxx can't determine superclass of missing type xxxxx`这样的错误，可能是与你的某个库冲突了，这时需加上

```groovy
aspectjx {
    excludeJarFilter 'xxx'  //xxx为会产生冲突的库名 
}
```

你可能还会遇到这个坑`Error:Execution failed for task ':app:transformClassesWithExtractJarsForDebug'. > error`

据说这个是Instant Run 的一个Bug，设置里关闭Instant Run功能解决。

如果发现灵异的缓存现象，以前插入的代码删除了还是被插入，删除App后重装就正常了。。。。。

# AspectJ术语

- JoinPoints：代码可插入的点，比如一个方法的调用处或者方法内部。
- Pointcuts：切点，用来描述 JoinPoints 注入点的一段表达式。
- Aspect：切面，Pointcuts和 Advice 合在一起称作 Aspect。
- Advice：相关处理，常见的有 Before、After、Around 等，表示代码执行前、执行后、替换目标代码，也就是在 Pointcut 处插入代码。

# 常见用法

## 基础用法
具体代码如下：

### 首先在主Activity中调用测试方法

```java
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "基础用法测试";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBeforeAndAfter();
        testAround();
    }

    private void testBeforeAndAfter(){
        Log.d(TAG, "testBeforeAndAfter: 原本需执行的代码");
    }

    private void testAround(){
        Log.d(TAG, "testAround: 原本需执行的代码");
    }
}
```

新建一个切面类BaseAspect，用于基本用法测试

### @Aspect注解切面类名

这一步很重要，不然AspectJ找不到Aspect切面，也找不到Advice的相关处理
@Before 在插入点之前插入
@After 在插入点之后插入

```java
@Aspect
public class BaseAspect {

    private static final String TAG = "基础用法测试";

    @Before("execution(* com.jessie.aspectjdemo.MainActivity.testBeforeAndAfter(..))")
    public void activityOnMethodBefore(JoinPoint joinPoint){
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "activityOnMethodBefore: 调用方法路径" + key);
    }


    @After("execution(* com.jessie.aspectjdemo.MainActivity.testBeforeAndAfter(..))")
    public void activityOnMethodAfter(JoinPoint joinPoint){
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "activityOnMethodAfter: 调用方法路径" + key);

    }


    @Around("execution(* com.jessie.aspectjdemo.MainActivity.testAround(..))")
    public void activityOnMethodAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String key = proceedingJoinPoint.getSignature().toString();

        Log.d(TAG,"tivityOnMethodAroundFirst:在" + key + "方法之前调用 ");
        proceedingJoinPoint.proceed();
        Log.d(TAG, "activityOnMethodAroundSecond:在" + key+"方法之后调用 ");
    }

}
```
### 注解括号内的表达式说明

第一个`*`表示返回值。

第二个`*`表示返回值为任意类型。

后面如`com.jessie.aspectjdemo.MainActivity.testBeforeAndAfter(..)`方法的全路径。

其中可以用`*`来进行通配，几个`*`没区别。如`android.app.Activity.on**(..)`

可以通过『&&、||、!』来进行条件组合。`(..)`代表这个方法的参数，可以指定类型，或者`(..)`这样来代表任意类型、任意个数的参数。

如果发现没有回调（插入失败是没有错误提示的。。。）说明方法匹配失败，仔细检查方法的全路径，当`()`内参数为空时应填写`(..)`来进行匹配。

| 路径          | 含义                     |
| ----------- | ---------------------- |
| aaa.bbb.ccc | 全路径                    |
| aaa.*.ccc   | 匹配aaa包下的任何“一级子包”下的ccc类 |
| aaa.*       | 匹配aaa包及任何子包下的任何类       |

| 参数                     | 含义                                       |
| ---------------------- | ---------------------------------------- |
| (..)                   | 表示匹配接受任意个参数的方法                           |
| (..,java.lang.Integer) | 表示匹配接受java.lang.Integer类型的参数结束，且其前边可以接受有任意个参数的方法 |
| (java.lang.Integer,..) | 表示匹配接受java.lang.Integer类型的参数开始，且其后边可以接受任意个参数的方法 |
| (*,java.lang.Integer)  | 表示匹配接受java.lang.Integer类型的参数结束，且其前边接受有一个任意类型参数的方法 |

### 获取插入点方法的所有参数

```
joinpoint.getArgs();//参数列表
joinpoint.getTarget().getClass().getName();//类全路径
joinpoint.getSignature().getDeclaringTypeName();//接口全路径
joinpoint.getSignature().getName();//调用的方法名
```


可以用`JoinPoint`或`ProceedingJoinPoint`。

两者的区别：
`JoinPoint`是父类，提供获取拦截方法的信息的功能，如所有参数`joinpoint.getArgs()`
`ProceedingJoinPoint`是子类，只能用在`@Around`中，除了提供`JoinPoint`的所有功能外，还能提供方法的运行`proceed()`和`proceed(args)`功能。

![效果图][0]
## 自定义Pointcuts

先新建一个自定义切点类，用@Retention，@Target注解类名

```java
@Retention(RetentionPolicy.CLASS) //声明注解类
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD}) //作用于构造方法和方法
public @interface MyPointcuts {

}
```

在主Activity中定义测试方法，都用自定义切点@MyPointcuts注解

```java
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
```

通过三个按钮分别调用这3个方法

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    >

    <android.support.v7.widget.AppCompatButton
        android:text="测试自定义切点1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="testMyPointcuts1"/>

    <android.support.v7.widget.AppCompatButton
        android:text="测试自定义切点2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="testMyPointcuts2"/>

    <android.support.v7.widget.AppCompatButton
        android:text="测试自定义切点3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="testMyPointcuts3"/>
</LinearLayout>

```

新建自定义切点用法的切面类MyPointcutsAspect，记得@Aspect注解类名

```java
@Aspect
public class MyPointcutsAspect {
    private static final String MY_POINTCUTS_TAG = "自定义切点用法测试";

    @Pointcut("execution(@com.jessie.aspectjdemo.MyPointcuts * *(..))")
    public void myPointcutsMethod() {
    }

    @Around("myPointcutsMethod()")
    public void onMyPointcutsMethodAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String key = proceedingJoinPoint.getSignature().toString();
        Log.d(MY_POINTCUTS_TAG, "方法调用之前插入: " + key);
        proceedingJoinPoint.proceed();
        Log.d(MY_POINTCUTS_TAG, "方法调用之后插入: " + key);
    }

}
```

所有被自定义切点@MyPointcuts注解的方法都会插入代码，实现了插入多个切点的效果。

效果gif如下：

![效果gif][1]

## execution与call的区别

除了execution表达式外常用到的还有call表达式，它们之间的区别是用execution插入的代码是调用在被插入的方法内，用call插入的代码是调用在被插入的方法外。

## withincode组合自定义withincode

通过withincode和自定义withincode可实现代码的精准插入，当两个方法同时调用一个公共方法时，可以选定只在其中一个方法调用公共方法时插入。

在主Activity中定义两个测试方法和一个公共方法

```java
    public void testWithcode1(View view){
        testComment();
    }

    public void testWithcode2(View view){
        testComment();
    }

    private void testComment(){
        Log.d(WITH_CODE, "testComment: 公共方法执行的代码");
    }
```

通过布局文件的两个按钮分别调用

```xml
 <android.support.v7.widget.AppCompatButton
        android:text="测试精准插入1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="testWithcode1"/>

<android.support.v7.widget.AppCompatButton
        android:text="测试精准插入2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="testWithcode2"/>
```

新建精确插入代码用法的切面类WithcodeAspect，记得@Aspect注解类名

我们让只有testWithcode2调用公共方法时才插入代码

```java
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

    @After("testWithcode2MethodOnlyInvoke()")
    public void beforeWithcode2MethodOnlyInvoke(JoinPoint joinPoint) {
        String key = joinPoint.getSignature().toString();
        Log.d(WITH_CODE, "beforeWithcode2MethodOnlyInvoke: " + key);
    }

}
```

效果gif如下：

![效果gif][2]

可见第一个方法调用时没有被插入代码，第二个方法被调用时插入了代码，我们实现了精准插入。

[0]:http://oqujmbgen.bkt.clouddn.com/AOP-Started.png
[1]:http://oqujmbgen.bkt.clouddn.com/AOP-Started1.gif
[2]:http://oqujmbgen.bkt.clouddn.com/AOP-Started2.gif
