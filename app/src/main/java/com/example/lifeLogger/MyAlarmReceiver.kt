package com.example.lifeLogger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.lifeLogger.Constant.Companion.CHANNEL_ID_1
import com.example.lifeLogger.Constant.Companion.CHANNEL_ID_2
import com.example.lifeLogger.Constant.Companion.NOTIFICATION_ID
import java.time.LocalDateTime

class MyAlarmReceiver: BroadcastReceiver()  {
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(p0: Context?, p1: Intent?) {
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        println("==================알람매니저====================")
        if(p1?.extras?.get("code") == MainActivity.REQUEST_CODE) {
            Toast.makeText(p0, "Alarm Start", Toast.LENGTH_SHORT).show()
            var count1 = p1.getIntExtra("count1", 0)
            var count2 = p1.getIntExtra("count2", 0)
            println("count1 : $count1")
            println("count2 : $count2")
            Log.d("myLog", "count : $count1")
            if (p0 != null) {
                if(count1 == 32){
                    println("아침알람")
                    notificationManager = p0.getSystemService(
                        Context.NOTIFICATION_SERVICE) as NotificationManager
                    createNotificationChannel(0)
                    deliverNotification(p0, 0)
                } else if(count1 == 33){
                    println("오후알람")
                    notificationManager = p0.getSystemService(
                        Context.NOTIFICATION_SERVICE) as NotificationManager
                    createNotificationChannel(1)
                    deliverNotification(p0, 1)

                }

            }
        }
    }
    // Notification 을 띄우기 위한 Channel 등록
    private fun createNotificationChannel(NOTIFICATION_ID_NUM:Int ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            var notificationChannel = NotificationChannel(
                "", // 채널의 아이디

                "오전 알림", // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
                /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
                */
            )

            if(NOTIFICATION_ID_NUM == 0){
                notificationChannel = NotificationChannel(
                    CHANNEL_ID_1,
                    "오전 알림",
                    NotificationManager.IMPORTANCE_HIGH
                )
            }else if(NOTIFICATION_ID_NUM == 1){
                notificationChannel = NotificationChannel(
                    CHANNEL_ID_2,
                    "오후 알림",
                    NotificationManager.IMPORTANCE_HIGH
                )
            }
            notificationChannel.enableLights(true) // 불빛
            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.vibrationPattern = (longArrayOf(50, 300)) //진동 패턴
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = "앱 실행 알림 오전." // 채널 정보
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    // Notification 등록
    private fun deliverNotification(context: Context,NOTIFICATION_ID_NUM:Int ){
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_NUM, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_MUTABLE
            /*
            1. FLAG_UPDATE_CURRENT : 현재 PendingIntent를 유지하고, 대신 인텐트의 extra data는 새로 전달된 Intent로 교체
            2. FLAG_CANCEL_CURRENT : 현재 인텐트가 이미 등록되어있다면 삭제, 다시 등록
            3. FLAG_NO_CREATE : 이미 등록된 인텐트가 있다면, null
            4. FLAG_ONE_SHOT : 한번 사용되면, 그 다음에 다시 사용하지 않음
             */
        )
        val dateAndtime: LocalDateTime = LocalDateTime.now()
        if(NOTIFICATION_ID_NUM == 0){
            val builder = NotificationCompat.Builder(context, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_launcher1_foreground) // 아이콘
                .setContentTitle("앱을 실행해 주세요.$dateAndtime") // 제목
                .setContentText("알림을 터치 하여 앱을 실행해 주세요.") // 내용
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }else if(NOTIFICATION_ID_NUM == 1){
            val builder = NotificationCompat.Builder(context, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_launcher1_foreground) // 아이콘
                .setContentTitle("앱을 실행해 주세요.$dateAndtime") // 제목
                .setContentText("알림을 터치 하여 앱을 실행해 주세요.") // 내용
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }

    }
}

class Constant {
    companion object {
        // 아이디 선언
        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID_1 = "notification_channel1"
        const val CHANNEL_ID_2 = "notification_channel2"

        // 알림 시간 설정
        const val ALARM_TIMER = 5
    }
}