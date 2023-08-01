package com.example.lifeLogger

import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import com.example.lifeLogger.databinding.ActivityMainBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var setting: SharedPreferences

    companion object {
        const val REQUEST_CODE = 101
    }

    //뒤로가기 키 관련 변수
    private var backKeyPressedTime : Long = 0
    private var terminationTime : Long = 2500
    var operation = false

    //뷰 바인딩

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        //알람 설정
        setContentView(binding.root)
        setting = getSharedPreferences("setting", MODE_PRIVATE)
        binding.toggleButton01.isChecked = setting.getBoolean("alarm", false)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        PendingIntent.getBroadcast(
            binding.root.context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
        val alarmManager = binding.root.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var pendingIntent:PendingIntent = Intent(binding.root.context, MyAlarmReceiver::class.java).let {
            it.putExtra("code", REQUEST_CODE)
            it.putExtra("count1", 32)
            PendingIntent.getBroadcast(binding.root.context, REQUEST_CODE,  it, PendingIntent.FLAG_MUTABLE)
        }


        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var today: Long = System.currentTimeMillis()

        if(binding.toggleButton01.isChecked){

            alarmManager.cancel(pendingIntent)
            println("펜딩인텐트 삭제")
            var pendingIntent:PendingIntent = Intent(binding.root.context, MyAlarmReceiver::class.java).let {
                it.putExtra("code", REQUEST_CODE)
                it.putExtra("count1", 32)
                PendingIntent.getBroadcast(binding.root.context, REQUEST_CODE,  it, PendingIntent.FLAG_MUTABLE)
            }

            val calendar1 = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 15)
                set(Calendar.MINUTE, 35)
                set(Calendar.SECOND, 0)
            }

            val aTime = System.currentTimeMillis()
            val interval = (1000 * 60 * 60 * 24).toLong()

            while(aTime>calendar1.timeInMillis){
                calendar1.timeInMillis += interval;
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar1.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            println("시간: ${df.format(aTime)}")
            println("시간: ${df.format(calendar1.timeInMillis)}")
            /*
            val calendar2 = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 14)
                set(Calendar.MINUTE, 35)
            }

            while(aTime>calendar2.timeInMillis){
                calendar2.timeInMillis += interval;
            }
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar2.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent2
            )
            println("시간: ${df.format(aTime)}")
            println("시간: ${df.format(calendar2.timeInMillis)}")
            */

            Toast.makeText(applicationContext, "Start", Toast.LENGTH_SHORT).show()
            Log.d("myLog", "Start")
        }

        binding.toggleButton01.setOnCheckedChangeListener { _, b ->
            setting.edit {
                putBoolean("alarm", b)
            }

            if(b) {
                // Case 0: 1회성 알람, 10초 후
                /*
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000 * 10,
                    pendingIntent
                )
                */
                // Case 1: 10초 후 10초 간격으로 Alarm (Min Interval: 1 minute)
                /*
                alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000 * 10,
                    1000 * 10L,
                    pendingIntent
                )
                 */


                // Case 2: 10초 후 2분 간격으로 Alarm (Interval: 2 minute)
//            alarmManager.setInexactRepeating(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 1000 * 10,
//                1000 * 60L * 2,
//                pendingIntent
//            )

                // Case 3: 오전 8시 27분 Alarm 생성 (Interval: Day)

                val calendar1 = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
                System.out.println("시간1_toggle : ${df.format(calendar1.timeInMillis)}")
                System.out.println("시간1_toggle : ${df.format(today)}")

                val aTime = System.currentTimeMillis()
                val interval = (1000 * 60 * 60 * 24).toLong()

                while(aTime>calendar1.timeInMillis){
                    calendar1.timeInMillis += interval;
                }


                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar1.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

                System.out.println("시간2_toggle : ${df.format(calendar1.timeInMillis)}")
                System.out.println("시간2_toggle : ${df.format(today)}")
                Toast.makeText(applicationContext, "Start", Toast.LENGTH_SHORT).show()
                Log.d("myLog", "Start")

            } else {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(applicationContext, "Cancel", Toast.LENGTH_SHORT).show()
                Log.d("myLog", "Cancel")
            }
        }




        if(!isRunningService(this,"com.example.biocheck", "BackService")){
            /*
            Intent(this, BackService::class.java).also { intent ->
                startService(intent)
            }
            */
        }
        Intent(this, BackService::class.java).also { intent ->
            startService(intent)
        }
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root



        val pm2 = getSystemService(POWER_SERVICE) as PowerManager
        val packageName = packageName
        if (pm2.isIgnoringBatteryOptimizations(packageName)) {
        } else {    // 메모리 최적화가 되어 있다면, 풀기 위해 설정 화면 띄움.
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 0)
        }

        // 버튼 정의
        val logout_button = findViewById<Button>(R.id.logout_button)
        val close_app_button = findViewById<Button>(R.id.close_app_button)

        // 로그아웃 버튼 이벤트
        logout_button.setOnClickListener {
            val auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
            val autoLoginEdit = auto.edit()
            autoLoginEdit.clear()
            autoLoginEdit.commit()

            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        // 앱종료 버튼 이벤트
        close_app_button.setOnClickListener {
            finishAffinity()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        System.out.println("dddddddddddddddddddddddddd")
        when (item.itemId) {
            R.id.it_menu_item_1 ->
                System.out.println("툴바1")
            R.id.it_menu_item_2 ->
                System.out.println("툴바1")
        }
        return super.onOptionsItemSelected (item);
    }
    public override fun onResume() {
        super.onResume()
    }

    // 현재 포그라운드 앱 패키지 로그로 띄우는 함수
    inner class CheckPackageNameThread : Thread() {
        override fun run() {
            while (operation) {

                // 실행 중인 앱 패키지 이름 출력
                //println(getPackageName(applicationContext))
                try {
                    // 2초
                    sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                //테스트
            }
        }
    }

    override fun onBackPressed() {

        if(System.currentTimeMillis() > backKeyPressedTime + terminationTime){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "앱을 종료하시려면 한번 더 누르세요.", Toast.LENGTH_SHORT).show()
            return
        }else if(System.currentTimeMillis() <= backKeyPressedTime + terminationTime){
            finishAffinity()
        }
    }
}

fun isRunningService(context: Context, packageName: String, serviceClassName: String): Boolean {
    var isRunningService = false
    val target = "$packageName.$serviceClassName"
    val activityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
    for (serviceInfo in activityManager.getRunningServices(Int.MAX_VALUE)) {
        println("서비스 인포" + serviceInfo.service.className)
        println("타겟" + target)

        if (target.equals(serviceInfo.service.className)) {
            isRunningService = true
            break
        }
    }
    return isRunningService
}