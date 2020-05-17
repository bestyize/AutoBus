# AutoBus

## 1、介绍

一个自写的发布订阅框架,主要作用是实现消息在组件之间的传递。
目前支持两种模式，一种需要自己注册和取消订阅事件（LiteBus）
另一种通过绑定activity生命周期来实现不需要自己注册或者取消事件(AutoBus)

第一种配置稍微麻烦一点，但是比较通用，普通类也可以使用
第二种不需要配置，拿来即用，但只能用在activity或者fragment中

## 2、AutoBus使用方式

1、定义消息类型

```java
    class MyMessage{
        public final String msg;

        public MyMessage(String msg) {
            this.msg = msg;
        }
    }
```

2、在需要接受消息的方法上面声明注解,并且声明方法的工作线程

```java
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void receiver(MyMessage message){
        Log.i("MainActivity",message.msg);
    }
```

3、使用AutoBus发送消息

```java
    AutoBus.with(MainActivity.this).post(new MyMessage("Hello AutoBus"));
```

## 3、LiteBus使用方式

1、定义消息类型

```java
    class MyMessage{
        public final String msg;

        public MyMessage(String msg) {
            this.msg = msg;
        }
    }
```

2、在需要接受消息的方法上面声明注解,并且声明方法的工作线程

```java
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void receiver(MyMessage message){
        Log.i("MainActivity",message.msg);
    }
```

3、在onCreate中注册当前类

```java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiteBus.defaultBus().register(this);
        ...
        ...
    }
```

4、在某个线程中发送消息

```java
    LiteBus.defaultBus().publish(new MyMessage("Hello LiteBus"));
```

5、重写onDestroy()取消当前类的注册

```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiteBus.defaultBus().unregister(this);
    }
```

## 4、引入依赖

1、添加jitpack.io仓库(build.gradle(project))

```gradle
    buildscript {
        repositories {
            maven { url 'https://jitpack.io' }
            google()
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:3.6.3'
            // NOTE: Do not place your application dependencies here; they belong
            // in the individual module build.gradle files
        }
    }
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
            google()
            jcenter()
        }
    }
```

2、引入AutoBus依赖(build.gradle(app))

```gradle
    implementation 'com.github.bestyize:AutoBus:v1.0.3'
```
