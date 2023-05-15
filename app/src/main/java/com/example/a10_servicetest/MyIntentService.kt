package com.example.a10_servicetest

import android.app.IntentService
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.TextView

class MyIntentService : IntentService("MyIntentService"){

    //广播标志符
    companion object {
        public val ACTION_UPDATE_MAIN_ACTIVITY: String =
            "com.example.servicetest.UPDATE_MAIN_ACTIVITY"
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d("www","intentServiceDoing . thread is ${Thread.currentThread().name}")

        //获取待传递过来的数据
        val updateText:Int? = intent?.getIntExtra("updateText" , 0)

        //发送广播,把需要的数据放入intent中
        val broadcastIntent:Intent = Intent()
        broadcastIntent.setAction(ACTION_UPDATE_MAIN_ACTIVITY)
        broadcastIntent.putExtra("updateText",1)        //发送数据
        sendBroadcast(broadcastIntent)
        Log.d("www","intentService已发送广播")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("www","intentService被destroy")
    }
}