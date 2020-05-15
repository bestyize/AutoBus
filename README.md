# LiteBus

一个自写的发布订阅框架,主要作用是实现消息在组件之间的传递。
目前支持两种模式，一种需要自己注册和取消订阅事件（LiteBus）
另一种通过绑定activity生命周期来实现不需要自己注册或者取消事件

第一种配置稍微麻烦一点，但是比较通用，普通类也可以使用
第二种不需要配置，拿来即用，但只能用在activity或者fragment中

# 使用方式

在需要接受消息的方法上面声明注解

在生命周期结束时销毁订阅


下面是一个用例

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    ActivityMainBinding vb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LiteBus.defaultBus().register(this);
        super.onCreate(savedInstanceState);
        vb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());
        vb.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj=new Object();
                System.out.println(obj);
                LiteBus.defaultBus().publish(new MyMessage("同步消息"));
            }
        });
        vb.btnSendAsyncMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count=20;
                        while (count-->0){
                            try {
                                LiteBus.defaultBus().publish(new MyMessage("异步消息"));
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }).start();
            }
        });
        vb.btnOpenSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });


    }
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void finder(MyMessage obj){
        Log.i(TAG,obj.msg);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiteBus.defaultBus().unregister(this);
    }
```
