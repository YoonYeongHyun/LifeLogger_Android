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


    //위치관련 프로퍼티
    private var text_view_id: TextView? = null
    private var latitudeView: TextView? = null
    private var longitudeView: TextView? = null
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    //전화수 관련
    private var callNum: TextView? = null
    private var callNo: Int? = 0

    //뷰 바인딩
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


        //수집정보 뷰
        text_view_id = findViewById<View>(R.id.text_view_id) as TextView
        text_view_id!!.movementMethod = ScrollingMovementMethod()

        // 버튼 정의
        val start_button = findViewById<Button>(R.id.start_button)
        val end_button = findViewById<Button>(R.id.end_button)




        // 시작 버튼 이벤트
        start_button.setOnClickListener { // 권환 허용이 안되어 있으면 권환 설정창으로 이동
            //MainActivity().getInstance()?.updateTheTextView("전화중", true)
            /*
            operation = true
            checkPackageNameThread = CheckPackageNameThread()
            checkPackageNameThread!!.start()
            */
        }

        // 종료 버튼 이벤트
        end_button.setOnClickListener {
            //MainActivity().getInstance()?.updateTheTextView("전화종료", false)
            //operation = false
        }
    }

    public override fun onResume() {
        super.onResume()
        Toast.makeText(this, "activity onResume", Toast.LENGTH_SHORT).show()
        //sensorManager!!.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        //위치정보관련
        getMylocation()


        //서비스 실행 서비스 실행안할 시 에만 실행
        /*
        if(!isRunningService(this, "com.example.biocheck","BackService")){
            Intent(this, BackService::class.java).also { intent ->
                startService(intent)
            }
        }
        */

        //서비스 실행 실행중 유무 상관없이
        Intent(this, BackService::class.java).also { intent ->
            startService(intent)
        }
    }
/*
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            //센서 이벤트가 발생할때 마다 걸음수 증가
            currentSteps++
            stepCountView!!.text = currentSteps.toString()
        }
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            light = event.values[0].toString()
            lightView!!.text = light
        }
    }
    //override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
*/
    // 현재 포그라운드 앱 패키지 로그로 띄우는 함수
    inner class CheckPackageNameThread : Thread() {
        var textView = findViewById<View>(R.id.text_view_id) as TextView
        override fun run() {
            while (operation) {

                // 실행 중인 앱 패키지 이름 출력
                println(getPackageName(applicationContext))
                textView.append(getPackageName(applicationContext) + "\n")
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

    companion object {

        private var instance:MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }

        // 자신의 앱의 최소 타겟을 롤리팝 이전으로 설정
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // 현재 포그라운드 앱 패키지를 가져오는 함수
        fun getPackageName(context: Context): String {

            // UsageStatsManager 선언
            val usageStatsManager =
                context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

            // 마지막 실행 앱 타임스탬프
            var lastRunAppTimeStamp = 0L

            // 얼마만큼의 시간동안 수집한 앱의 이름을 가져오는지 정하기 (begin ~ end 까지의 앱 이름을 수집한다)
            val INTERVAL = (1000 * 60 * 60 * 24).toLong()
            val end = System.currentTimeMillis()
            val begin = end - INTERVAL // 5분전
            val packageNameMap: LongSparseArray<String> = LongSparseArray<String>()

            // 수집한 이벤트들을 담기 위한 UsageEvents
            val usageEvents = usageStatsManager.queryEvents(begin, end)

            // 이벤트가 여러개 있을 경우 (최소 존재는 해야 hasNextEvent가 null이 아니니까)
            while (usageEvents.hasNextEvent()) {

                // 현재 이벤트를 가져오기
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)

                // 현재 이벤트가 포그라운드 상태라면(현재 화면에 보이는 앱이라면)
                if (isForeGroundEvent(event)) {

                    // 해당 앱 이름을 packageNameMap에 넣는다.
                    packageNameMap.put(event.timeStamp, event.packageName)
                    //packageNameMap.put(event.getTimeStamp(), event.getAppStandbyBucket());


                    // 가장 최근에 실행 된 이벤트에 대한 타임스탬프를 업데이트 해준다.
                    if (event.timeStamp > lastRunAppTimeStamp) {
                        lastRunAppTimeStamp = event.timeStamp
                    }
                }
            }
            // 가장 마지막까지 있는 앱의 이름을 리턴해준다.
            return packageNameMap.get(lastRunAppTimeStamp).toString()
        }

        // 앱이 포그라운드 상태인지 체크
        private fun isForeGroundEvent(event: UsageEvents.Event?): Boolean {

            // 이벤트가 없으면 false 반환
            return if (event == null) false else event.eventType == UsageEvents.Event.ACTIVITY_RESUMED

            // 이벤트가 포그라운드 상태라면 true 반환
            //if(BuildConfig.VERSION_CODE >= 29)

            //return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
        }




    }

    fun getInstance(): MainActivity? {
        return mainActivity;
    }


    fun updateTheTextView(s: String, flag: Boolean) {
        if (flag) {
            println("시작버튼 flag : $flag")
            /*
            System.out.println("녹음준비")
            val fileName: String = Date().getTime().toString() + ".mp3"
            outputPath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputPath) // 저장위치
                prepare()
            }
            System.out.println("녹음시작")
            mediaRecorder?.start()

             */
            val fileName: String = Date().getTime().toString() + ".mp3"
            outputPath =
                Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치
            mediaRecorder = MediaRecorder()
            mediaRecorder!!.setAudioSource((MediaRecorder.AudioSource.MIC))
            mediaRecorder!!.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder!!.setOutputFile(outputPath)


            try {
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                onCall = true
                //Toast.makeText(this, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (flag == false) {
            println("중단버튼 flag : $flag")
            /*
            System.out.println("녹음중단")
            mediaRecorder?.run {
                System.out.println("stop()")
                mediaRecorder?.stop()

                System.out.println("release()")
                mediaRecorder?.release()
            }
            mediaRecorder = null
             */

            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            onCall = false


            //Toast.makeText(this, "녹음이 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //위치 관련 메서드
    private fun getMylocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        setLocationListener()
    }

    @Suppress("MissingPermission")
    private fun setLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )

            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast
                .makeText(
                    this@MainActivity,
                    "${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                )
                .show()
            latitudeView = findViewById<View>(R.id.latitudeView) as TextView
            longitudeView = findViewById<View>(R.id.longitudeView) as TextView

            latitudeView?.text = location.latitude.toString()
            longitudeView?.text = location.longitude.toString()

            removeLocationListener()
        }

        private fun removeLocationListener() {
            if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
                locationManager.removeUpdates(myLocationListener)
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

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var intentmesage = intent.action
        val dateAndtime: LocalDateTime = LocalDateTime.now()
        val onlyDate: LocalDate = LocalDate.now()

        // 리시버 상태 체크
        Toast.makeText(context, "Event !!!", Toast.LENGTH_SHORT).show()
        val telephonyManager =
            context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if (phoneNumber == null) {
            Log.i("call-state", " : NULL")
        } else {
            if (TelephonyManager.EXTRA_STATE_OFFHOOK == state) {
                System.out.println("call Active")
                println("전화받은 시간: $dateAndtime")
                Toast.makeText(context, "call Active", Toast.LENGTH_SHORT).show()
                MainActivity().getInstance()?.updateTheTextView("전화중", true)
            } else if (TelephonyManager.EXTRA_STATE_IDLE == state) {
                System.out.println("No call")
                println("전화종료 시간: $dateAndtime")
                MainActivity().getInstance()?.updateTheTextView("전화종료", false)
                Toast.makeText(context, "No call", Toast.LENGTH_SHORT).show()
            } else if (TelephonyManager.EXTRA_STATE_RINGING == state) {
                println("전화온 시간: $dateAndtime")
                Toast.makeText(context, "Ringing State : $phoneNumber", Toast.LENGTH_SHORT).show()
            }
        }

        if ("android.provider.Telephony.SMS_RECEIVED" == intent.action) {

            val bundle = intent?.extras
            val objs = bundle?.get("pdus") as Array<Any>?
            val messages: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(objs!!.size)
            for (i in objs!!.indices) {
                messages[i] = SmsMessage.createFromPdu(objs[i] as ByteArray)
            }
            System.out.println("문자 수신!!")
            System.out.println(messages[0]?.messageBody.toString())
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