package com.example.a10_servicetest

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.KeyEvent.ACTION_UP
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


//在IntentService发送广播请求更改Activity的学号姓名
//在Service启动后,发送广播请求更改Activity的进度条,需要绑定
//Service绑定用于前台Service
class MainActivity : AppCompatActivity() {

    //获取Service的内部绑定类类对象
    lateinit var downloadBinder: MyService.DownloadBinder
    private var bound = false       //判断Service是否绑定到了Activity

    //用于连接Service和Activity
    private val connection = object : ServiceConnection{            //匿名类对象

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            downloadBinder = service as MyService.DownloadBinder            //Activity通过这个内部类对象实例与Service相互联系
            downloadBinder.startDownload()
            downloadBinder.getProgress()
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }

    //接收广播并根据Service发来的数据进行UI更新
    private val broadcastReceiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val progress = intent?.getIntExtra("progress", 0)       //获取进度值
            myProgress.progress = progress ?: 0             //处理null

            progressNum.text = progress.toString() ?: "0"
        }

    }



//    //使用Handle更新UI:学号和姓名
    val updateText = 1
    val handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
           //进行UI操作
            when( msg.what){
                updateText -> {
                    Log.d("www","handler接收指令1,进行UI操作")
                    name.setText("王熙乐")
                    stuId.setText("20212005330")
                }

            }
        }
    }

    //广播接收匿名类
    val myBroadcastReceiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            //获取传递到数据
            val updateText : Int? = intent?.getIntExtra("updateText",0)
            Log.d("www","已接受到广播的数据, updatext 为 $updateText")

            //数据传递给Handle处理
            val message:Message = Message.obtain()
            message.what = 1                //updateText无法在这里使用好像.1表示更新文本框
            handler.sendMessage(message)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //为Service注册广播接收其
        val intentFilter2 = IntentFilter(MyService.UPDATE_PROGRESS_BAR)
        registerReceiver(broadcastReceiver, intentFilter2)

        // 为IntentService注册广播接收器
        val intentFilter = IntentFilter(MyIntentService.ACTION_UPDATE_MAIN_ACTIVITY)
        registerReceiver(myBroadcastReceiver, intentFilter)


        //Service启动与关闭,绑定与解绑
        startServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            startService(intent)
        }
        stopServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
        }
        startIntentServiceBtn.setOnClickListener {
            val intent = Intent(this, MyIntentService::class.java)
            startService(intent)
        }

        //绑定事件
        bindServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        unbindServiceBtn.setOnClickListener {
            unbindService(connection)
        }

    }
    //销毁广播,解绑Service
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
        unregisterReceiver(broadcastReceiver)
        if(bound){
            unbindService(connection)
            bound = false
        }
    }
}