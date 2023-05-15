package com.example.a10_servicetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

//创建子线程不断更新ProgressBar的进度
class MyService : Service() {

    private val myBinder = DownloadBinder()
    private var progress = 0            //进度条的进度值
    //广播标识符
    companion object{
        public val UPDATE_PROGRESS_BAR : String = "com.example.servicetest.UPDATE_PROGRESS_BAR"
    }

    //内部类用于管理功能
    class DownloadBinder : Binder(){
        fun startDownload(){
            Log.d("www","Downloading")
        }
        fun getProgress():Int{
            Log.d("www","getProgress")
            return 0;
        }
    }


    //绑定功能
    override fun onBind(intent: Intent): IBinder {
        return myBinder
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("www","Service onCreate executed")

    //构造前台Service
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager     //通知管理器
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {                     //创建通知渠道
            NotificationChannel("my_service", "前台Service通知", IMPORTANCE_DEFAULT)
        } else { TODO("VERSION.SDK_INT < O")
        }
        manager.createNotificationChannel(channel)

        //构建通知
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentIntent(pi)
            .setContentTitle("标题")
            .setContentText("内容nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn")
            .build()

        //开启前台Service显示,显示id为1
        startForeground(1, notification)
    }


    //发送广播给Activity,更新进度条
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("www", "Service onStartCommond executed, 创建子线程发送广播更新进度条的UI改变申请")
        thread {
            Log.d("www","thread is ${Thread.currentThread().name}")
            while( progress < 100){
                progress += 20
                Thread.sleep(1000)
            //发送广播
            val broadcaseIntent = Intent()
            broadcaseIntent.action = "com.example.servicetest.UPDATE_PROGRESS_BAR"
            broadcaseIntent.putExtra("progress", progress)          //数据传递给Activity
            sendBroadcast(broadcaseIntent)
            }
            stopSelf()      //停止
        }
//        如果 Service 在运行时被系统意外杀死了（例如，当系统资源紧张时，就可能会清除一些进程），则 Service 会重新启动，并尝试再次创建运行它的进程。
        return START_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("www", "Service onDestroy executed")
    }
}