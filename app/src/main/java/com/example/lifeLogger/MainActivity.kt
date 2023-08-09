package com.example.lifeLogger

import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeLogger.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private var terminationTime : Long = 1500 // <--- 얘사용

    //뷰 바인딩
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //실행 시 첫번째 탭 레이아웃 출력
        supportFragmentManager.beginTransaction().replace(R.id.containers, HomeFragment()).commit();
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val preIntent = intent
        if(!(preIntent.getStringExtra("Direction").isNullOrEmpty())){
            var Direction = preIntent.getStringExtra("Direction").toString()
            when (Direction) {
                "Home" -> {
                    bottomNavigation.selectedItemId = R.id.home
                    supportFragmentManager.beginTransaction().replace(R.id.containers, HomeFragment())
                        .commit()
                }
                "SurveyList"-> {
                    bottomNavigation.selectedItemId = R.id.surveyList
                    supportFragmentManager.beginTransaction().replace(R.id.containers, SurveyListFragment())
                        .commit()
                }
                "Config" -> {
                    bottomNavigation.selectedItemId = R.id.config
                    supportFragmentManager.beginTransaction().replace(R.id.containers, ConfigFragment())
                        .commit()
                }else ->{
                bottomNavigation.selectedItemId = R.id.home
                supportFragmentManager.beginTransaction().replace(R.id.containers, HomeFragment())
                    .commit()
                }
            }
        }

        //탭 선택 시 해당 레이아웃 출력
        bottomNavigation.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        supportFragmentManager.beginTransaction().replace(R.id.containers, HomeFragment())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.surveyList -> {
                        supportFragmentManager.beginTransaction().replace(R.id.containers, SurveyListFragment())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.config -> {
                        supportFragmentManager.beginTransaction().replace(R.id.containers, ConfigFragment())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            })
        //알람 설정
        setting = getSharedPreferences("setting", MODE_PRIVATE)

        //인텐트 생성
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        PendingIntent.getBroadcast(
            binding.root.context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
        //알람 매니저 설정
        val alarmManager = binding.root.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var pendingIntent:PendingIntent = Intent(binding.root.context, MyAlarmReceiver::class.java).let {
            it.putExtra("code", REQUEST_CODE)
            it.putExtra("count1", 32)
            PendingIntent.getBroadcast(binding.root.context, REQUEST_CODE,  it, PendingIntent.FLAG_MUTABLE)
        }

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        alarmManager.cancel(pendingIntent)
        println("펜딩인텐트 삭제")


        val calendar1 = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
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

        Intent(this, BackService::class.java).also { intent ->
            startService(intent)
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    //취소 키 두번 입력 앱종료
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

//백그라운드 서비스 실행중 인지 확인(일단 안씀)
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
