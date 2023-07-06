package com.example.biocheck

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import android.telephony.SmsMessage
import android.telephony.TelephonyManager
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.LongSparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.biocheck.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

class MainActivity : AppCompatActivity() {

    private var mainActivity: MainActivity? = this
    private var checkPackageNameThread: CheckPackageNameThread? = null
    private var context: Context = this

    //뒤로가기 키 관련 변수
    private var backKeyPressedTime : Long = 0
    private var terminationTime : Long = 2500


    var operation = false
    //녹음관련 프로퍼티
    private var outputPath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false

    //데시벨 관련 프로퍼티
    private var onCall: Boolean? = false
    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private var job: Job? = null
    private var filePath = ""
    private var decibel: TextView? = null



    //뷰 바인딩
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_main)
        mainActivity = this;
        context = this;

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