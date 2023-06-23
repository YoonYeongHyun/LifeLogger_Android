package com.example.biocheck

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.ExifInterface
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.provider.CallLog
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.sdkStatus
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import kotlin.concurrent.timer

class BackService : Service(), SensorEventListener {
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private var isRunning = false
    var light: Long = 0
    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null
    private var lightSensor: Sensor? = null


    private var recorder: MediaRecorder? = null
    private var job: Job? = null
    private var outputPath: String? = null

    var mStartMode // indicates how to behave if the service is killed
            = 0
    var mBinder // interface for clients that bind
            : IBinder? = null
    var mAllowRebind // indicates whether onRebind should be used
            = false

    // 메인 스레드로부터 메세지를 전달받을 핸들러 선언
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // 보통 서비스에서는 파일 다운로드 같은 작업을 수행함
            // 이 예제에서는 sleep. 5초를 주도록 함
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
            // 서비스를 사용하였다면 서비스를 종료해 주어야 함.
            // 아래 메소드는 작업 startId가 가장 최신일때만 서비스를 stop하게 함
            // 이렇게 하면 동시에 여러 작업할 때, 모든작업이 끝나야 stop이 된다
            // 이게뭔지는 이후 설명에서 나옴
            //stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // The service is being created
        Toast.makeText(this, "service oncreate", Toast.LENGTH_SHORT).show()

        val lis : SensorEventListener = this

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // 스레드의 루퍼를 얻어와 핸들러를 만들어준다.
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)

            var sec : Int = 0
            var flag :Boolean = true

            timer(period = 1000, initialDelay = 1000){
                println(sec)
                sec++;
                if(flag){
                    //sensorManager!!.registerListener(lis, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
                    flag = false
                }
                //cancel()
                //stopSelf()
                if(sec%10 == 0){
                    //sensorManager!!.unregisterListener(lis, lightSensor)
                    //cancel()
                    //stopSelf()

                    Handler(Looper.getMainLooper()).postDelayed({
                        //실행할 코드
                    }, 3000)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // The service is starting, due to a call to startService()
        Toast.makeText(this, "service onStart", Toast.LENGTH_SHORT).show()
        callInfo()
        smsInfo()
        pictureInfo()
        dbInfo()

        GlobalScope.launch { // launch a new coroutine in background and continue
            stepInfo()
            walkingInfo()
            sleepInfo()
            HeartRateInfo()
        }

        /*
        var sec : Int = 0
        timer(period = 1000, initialDelay = 1000){
            println(sec)
            sec++;
            if(sec == 10){
                cancel()
                stopSelf()
                Handler(Looper.getMainLooper()).postDelayed({
                //실행할 코드
                }, 3000)
            }
        }
         */

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager!!.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        if (stepCountSensor != null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            //
            sensorManager!!.registerListener(
                this,
                stepCountSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
        sensorManager!!.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)


        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        Toast.makeText(this, "service onBind", Toast.LENGTH_SHORT).show()

        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // All clients have unbound with unbindService()
        Toast.makeText(this, "service onUnbind", Toast.LENGTH_SHORT).show()
        return mAllowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    override fun onDestroy() {
        // The service is no longer used and is being destroyed
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")
        println("서비스 onDestroy")

    }

    //전화정보 가져오기
    private fun callInfo() {
        val resolver: ContentResolver = this.contentResolver
        val callLogUri = CallLog.Calls.CONTENT_URI
        var cursor: Cursor?

        try{
            cursor = resolver.query(callLogUri, null, null, null, "date DESC")
            if (cursor != null) {
                var maxcount = 30
                while (cursor.moveToNext()) {
                    if (--maxcount < 0) {
                        break
                    }
                    for (i in 0..cursor.columnCount-1) {
                        val columnName = cursor.getColumnName(i)
                        val cursorIndex = cursor.getColumnIndex(columnName)
                        //Log.i("Test_Log","cursor[${cursorIndex}] - name == $columnName // data = ${cursor.getString(cursorIndex)}")
                    }
                    val cal = Calendar.getInstance()
                    val df: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                    val date: Date = df.parse("197001010000")
                    cal.time = date
                    var plusMinute:Int = ((cursor.getString(0).toLong()/1000/60) + 540).toInt()
                    cal.add(Calendar.MINUTE, plusMinute)
                    val today : Long = System.currentTimeMillis()
                    val compareDate = ((today-cursor.getString(0).toLong())/1000/60/60/24)
                    if(compareDate < 7){
                        /*
                        System.out.println("ID : " + cursor.getString(43))
                        System.out.println("number : " + cursor.getString(13))
                        System.out.println("date : " + df.format(cal.time))
                        System.out.println("duration : " + cursor.getString(10))
                        System.out.println("type : " + cursor.getString(7))
                        */
                        //타입은 1-수신, 2-발신, 3-부재중, 5-수신거절
                        //val USER_ID = MyApi.Logined_id
                        val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
                        var USER_ID = auto.getString("userId", null);
                        val CALL_ID = cursor.getString(43)
                        val CALL_DURATION =cursor.getString(10).toInt()
                        val CALL_DATE = df.format(cal.time)
                        val CALL_TYPE =cursor.getString(7)
                        val CALL_NUMBER =cursor.getString(13)

                        if(USER_ID.isNullOrBlank()){
                            if(MyApi.Logined_id.isNullOrBlank()){
                                return
                            }else{
                                USER_ID = MyApi.Logined_id
                            }
                        }

                        val retrofit = RetrofitClient.getInstance()
                        val server = retrofit.create(InsertCallInfoAPI::class.java)

                        //API사용하여 통신
                        server.getInsertCallInfo(USER_ID, CALL_ID, CALL_DURATION, CALL_DATE, CALL_TYPE, CALL_NUMBER).enqueue(object :
                            Callback<stateModel> {
                            override fun onResponse(
                                call: Call<stateModel>,
                                response: Response<stateModel>
                            ) {
                                Log.d(MyApi.TAG, "통신 성공(통화) : ${response.body()}")
                            }
                            override fun onFailure(call: Call<stateModel>, t: Throwable) {
                                Log.d(MyApi.TAG, "통신 실패(통화) : ${t.localizedMessage}")
                            }
                        })
                    }


                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    //문자정보 가져오기
    private fun smsInfo(){
        //val uriSMSURI = Uri.parse("content://mms/inbox")
        //val uriSMSURI = Uri.parse("content://sms/inbox")
        val uriSMSURI = Uri.parse("content://sms/sent")
        //context?.grantUriPermission("com.example.biocheck", uriSMSURI, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        var aaa: String = "";
        val cursor: Cursor? = contentResolver.query(uriSMSURI, null, null, null, "date DESC")
        if (cursor != null) {
            var maxcount = 10
            while(cursor.moveToNext()){
                if (--maxcount < 0) {
                    break
                }
                /*
                System.out.println(cursor.columnCount)  //59개컬럼
                System.out.println(cursor.columnNames[0]) //_id
                System.out.println(cursor.count)
                */
                val cal = Calendar.getInstance()
                val df: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                val date:Date = df.parse("197001010000")
                cal.time = date
                var plusMinute:Int = ((cursor.getString(4).toLong()/1000/60) + 540).toInt()
                cal.add(Calendar.MINUTE, plusMinute)
                val today : Long = System.currentTimeMillis()
                val compareDate = ((today-cursor.getString(4).toLong())/1000/60/60/24)
                if(compareDate < 30){

                    val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
                    var USER_ID = auto.getString("userId", null);
                    val MESSAGE_ID = cursor.getString(0)
                    val MESSAGE_TEXT = cursor.getString(12)
                    val MESSAGE_DATE = df.format(cal.time)
                    val MESSAGE_NUMBER = cursor.getString(2)

                    if(USER_ID.isNullOrBlank()){
                        if(MyApi.Logined_id.isNullOrBlank()){
                           return
                        }else{
                            USER_ID = MyApi.Logined_id
                        }
                    }

                    val retrofit = RetrofitClient.getInstance()
                    val server = retrofit.create(InsertMessageInfoAPI::class.java)

                    //API사용하여 통신
                    server.getInsertMessageInfo(USER_ID, MESSAGE_ID, MESSAGE_TEXT, MESSAGE_DATE, MESSAGE_NUMBER).enqueue(object :
                        Callback<stateModel> {
                        override fun onResponse(
                            call: Call<stateModel>,
                            response: Response<stateModel>
                        ) {
                            Log.d(MyApi.TAG, "통신 성공(문자) : ${response.body()}")
                        }
                        override fun onFailure(call: Call<stateModel>, t: Throwable) {
                            Log.d(MyApi.TAG, "통신 실패(문자) : ${t.localizedMessage}")
                        }
                    })
                }
            }
        }
    }

    //사진정보 가져오기
    fun pictureInfo() {

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
        )
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs = arrayOf(
            dateToTimestamp(day = 1, month = 1, year = 2023).toString()
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        var cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            while (cursor.moveToNext()) {

                val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
                var USER_ID = auto.getString("userId", null);
                val PICTURE_ID = cursor.getLong(idColumn).toString()
                val PICTURE_TITLE = cursor.getString(titleColumn)
                var displayName = cursor.getLong(displayNameColumn)
                var PICTURE_DATE:String
                var PICTURE_TYPE:String = "1"

                if(USER_ID.isNullOrBlank()){
                    if(MyApi.Logined_id.isNullOrBlank()){
                        return
                    }else{
                        USER_ID = MyApi.Logined_id
                    }
                }

                val cal = Calendar.getInstance()
                val df: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                val date: Date = df.parse("197001010000")
                cal.time = date
                var plusMinute:Int = ((cursor.getLong(dateTakenColumn).toLong()/1000/60) + 540).toInt()
                cal.add(Calendar.MINUTE, plusMinute)
                val today : Long = System.currentTimeMillis()
                val compareDate = ((today-cursor.getLong(dateTakenColumn).toLong())/1000/60/60/24)
                if(compareDate < 7) {
                    val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PICTURE_ID.toString())
                    try {
                        var latitudeArray:List<String>
                        var longitudeArray:List<String>
                        var exif: ExifInterface? = null
                        var inPutStream: InputStream = contentResolver.openInputStream(contentUri)!!
                        exif = ExifInterface(inPutStream)
                        var latitudeString = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE).toString()
                        var longitudeString =
                            exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).toString() // 위도 latitude
                        var LATITUDE:String = "0"
                        var LONGITUDE:String = "0"
                        if (!latitudeString.isNullOrBlank()&&!latitudeString.equals("null")){
                            latitudeArray = latitudeString.split(",")
                            var latitude_1:Double = (latitudeArray[0].split("/"))[0].toDouble()/(latitudeArray[0].split("/"))[1].toDouble()
                            var latitude_2:Double = (latitudeArray[1].split("/"))[0].toDouble()/(latitudeArray[1].split("/"))[1].toDouble()
                            var latitude_3:Double = (latitudeArray[2].split("/"))[0].toDouble()/(latitudeArray[2].split("/"))[1].toDouble()
                            LATITUDE = (latitude_1 + latitude_2/60 + latitude_3/(3600)).toString()
                        }
                        if (!latitudeString.isNullOrBlank()&&!latitudeString.equals("null")){
                            longitudeArray = longitudeString.split(",")
                            var longitude_1:Double = (longitudeArray[0].split("/"))[0].toDouble()/(longitudeArray[0].split("/"))[1].toDouble()
                            var longitude_2:Double = (longitudeArray[1].split("/"))[0].toDouble()/(longitudeArray[1].split("/"))[1].toDouble()
                            var longitude_3:Double = (longitudeArray[2].split("/"))[0].toDouble()/(longitudeArray[2].split("/"))[1].toDouble()
                            LONGITUDE = (longitude_1 + longitude_2/60 + longitude_3/3600).toString()
                        }
                        PICTURE_DATE = df.format(cal.time)
                        //핸드폰 자체 캡쳐 이미지
                        if (displayName <= 0) {
                            PICTURE_TYPE = "0"
                            //continue;
                        }
                        val retrofit = RetrofitClient.getInstance()
                        val server = retrofit.create(InsertPictureInfoAPI::class.java)

                        //API사용하여 통신
                        server.getInsertPictureInfo(USER_ID, PICTURE_ID, PICTURE_TITLE, LATITUDE, LONGITUDE, PICTURE_DATE, PICTURE_TYPE).enqueue(object :
                            Callback<stateModel> {
                            override fun onResponse(
                                call: Call<stateModel>,
                                response: Response<stateModel>
                            ) {
                                Log.d(MyApi.TAG, "통신 성공(사진) : ${response.body()}")
                            }
                            override fun onFailure(call: Call<stateModel>, t: Throwable) {
                                Log.d(MyApi.TAG, "통신 실패(사진) : ${t.localizedMessage}")
                            }
                        })
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.targetException.printStackTrace()
                    }
                }

            }
        }
    }

    private suspend fun stepInfo() {
        if (sdkStatus(this) == 3) {
            // Health Connect is available and installed.
            val healthConnectClient = HealthConnectClient.getOrCreate(this)
            var current1 = Instant.now()
            val current2 = Instant.now()
            current1 = current1.minusSeconds(604800)
            readStepsByTimeRange(healthConnectClient,current1,current2)
        } else {
            println("헬스커넥트 설치X")
        }
    }

    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            formatter.parse("$day.$month.$year")?.time ?: 0
        }

    suspend fun readStepsByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant

    ) {
        try {

            val response =
                healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        dataOriginFilter = setOf(DataOrigin("com.sec.android.app.shealth")),
                        true,
                        1000,
                        null
                    )
                )
            for (stepRecord in response.records) {

                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                val df1: DateFormat = SimpleDateFormat("yyyyMMdd")
                val df2: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                val date1:Date = df1.parse("19700101")
                val date2:Date = df2.parse("197001010000")
                cal1.time = date1
                cal2.time = date2
                var plusMinute1:Int = ((stepRecord.startTime.plusSeconds(60*60*9).epochSecond)/60).toInt()
                var plusMinute2:Int = ((stepRecord.metadata.lastModifiedTime.plusSeconds(60*60*9).epochSecond)/60).toInt()
                cal1.add(Calendar.MINUTE, plusMinute1)
                cal2.add(Calendar.MINUTE, plusMinute2)
                //println(df1.format(cal1.time))
                //println(df2.format(cal2.time))

                val auto = getSharedPreferences("autoLogin", Service.MODE_PRIVATE)
                var USER_ID = auto.getString("userId", null);
                val STEP_COUNT = stepRecord.count.toInt()
                val STEP_DATE = df1.format(cal1.time)
                val LAST_STEP_DATE = df2.format(cal2.time)

                if(USER_ID.isNullOrBlank()){
                    if(MyApi.Logined_id.isNullOrBlank()){
                        return
                    }else{
                        USER_ID = MyApi.Logined_id
                    }
                }

                val retrofit = RetrofitClient.getInstance()
                val server = retrofit.create(InsertStepCountInfoAPI::class.java)

                //API사용하여 통신
                server.getInsertStepCountInfo(USER_ID, STEP_COUNT, STEP_DATE, LAST_STEP_DATE).enqueue(object :
                    Callback<stateModel> {
                    override fun onResponse(
                        call: Call<stateModel>,
                        response: Response<stateModel>
                    ) {
                        Log.d(MyApi.TAG, "통신 성공(걸음수) : ${response.body()}")
                    }
                    override fun onFailure(call: Call<stateModel>, t: Throwable) {
                        Log.d(MyApi.TAG, "통신 실패(걸음수) : ${t.localizedMessage}")
                    }
                })
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
    }

    private suspend fun walkingInfo() {
        if (sdkStatus(this) == 3) {
            // Health Connect is available and installed.
            val healthConnectClient = HealthConnectClient.getOrCreate(this)
            var current1 = Instant.now()
            val current2 = Instant.now()
            current1 = current1.minusSeconds(604800)
            readWalkingByTimeRange(healthConnectClient,current1,current2)
        } else {
            println("헬스커넥트 설치X")
        }
    }

    suspend fun readWalkingByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val response =
                healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        DistanceRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        dataOriginFilter = setOf(DataOrigin("com.sec.android.app.shealth")),
                        true,
                        1000,
                        null
                    )
                )

            for (distanceRecord in response.records) {
                // Process each step record
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                val df1: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                val date1:Date = df1.parse("197001010000")
                cal1.time = date1
                cal2.time = date1
                var plusMinute1:Int = ((distanceRecord.startTime.plusSeconds(60*60*9).epochSecond)/60).toInt()
                var plusMinute2:Int = ((distanceRecord.endTime.plusSeconds(60*60*9).epochSecond)/60).toInt()
                cal1.add(Calendar.MINUTE, plusMinute1)
                cal2.add(Calendar.MINUTE, plusMinute2)

                val auto = getSharedPreferences("autoLogin", Service.MODE_PRIVATE)
                var USER_ID = auto.getString("userId", null);
                val WALKING_DISTANCE = distanceRecord.distance.inMeters

                val START_TIME = df1.format(cal1.time)
                val END_TIME = df1.format(cal2.time)
                val WALKING_TIME = (distanceRecord.endTime.plusSeconds(32400).epochSecond -
                        distanceRecord.startTime.plusSeconds(32400).epochSecond)
                val WALKING_VELOCITY = (WALKING_DISTANCE*3600)/(WALKING_TIME*1000)

                if(USER_ID.isNullOrBlank()){
                    if(MyApi.Logined_id.isNullOrBlank()){
                        return
                    }else{
                        USER_ID = MyApi.Logined_id
                    }
                }

                val retrofit = RetrofitClient.getInstance()
                val server = retrofit.create(InsertWalkingInfoAPI::class.java)

                //API사용하여 통신
                server.getInsertWalkingInfo(USER_ID, WALKING_VELOCITY, START_TIME, END_TIME, WALKING_TIME, WALKING_DISTANCE).enqueue(object :
                    Callback<stateModel> {
                    override fun onResponse(
                        call: Call<stateModel>,
                        response: Response<stateModel>
                    ) {
                        Log.d(MyApi.TAG, "통신 성공(속도, 거리정보) : ${response.body()}")
                    }
                    override fun onFailure(call: Call<stateModel>, t: Throwable) {
                        Log.d(MyApi.TAG, "통신 실패(속도, 거리정보) : ${t.localizedMessage}")
                    }
                })


            }
        } catch (e: Exception) {
            // Run error handling here.
        }
    }

    private suspend fun sleepInfo() {
        if (sdkStatus(this) == 3) {
            // Health Connect is available and installed.
            val healthConnectClient = HealthConnectClient.getOrCreate(this)
            var current1 = Instant.now()
            val current2 = Instant.now()
            current1 = current1.minusSeconds(604800)
            readSleepByTimeRange(healthConnectClient,current1,current2)
        } else {
            println("헬스커넥트 설치X")
        }
    }

    suspend fun readSleepByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val response =
                healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        SleepSessionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        dataOriginFilter = setOf(DataOrigin("com.sec.android.app.shealth")),
                        true,
                        1000,
                        null
                    )
                )

            for (sleepRecord in response.records) {
                // Process each step record
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                val df1: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                val date1:Date = df1.parse("197001010000")
                cal1.time = date1
                cal2.time = date1
                var plusMinute1:Int = ((sleepRecord.startTime.plusSeconds(60*60*9).epochSecond)/60).toInt()
                var plusMinute2:Int = ((sleepRecord.endTime.plusSeconds(60*60*9).epochSecond)/60).toInt()
                cal1.add(Calendar.MINUTE, plusMinute1)
                cal2.add(Calendar.MINUTE, plusMinute2)

                val auto = getSharedPreferences("autoLogin", Service.MODE_PRIVATE)
                var USER_ID = auto.getString("userId", null);
                val START_TIME = df1.format(cal1.time)
                val END_TIME = df1.format(cal2.time)
                val SLEEP_TIME = (sleepRecord.endTime.plusSeconds(32400).epochSecond -
                        sleepRecord.startTime.plusSeconds(32400).epochSecond)
                /*
                println("USER_ID : $USER_ID")
                println("START_TIME : $START_TIME")
                println("END_TIME : $END_TIME")
                println("SLEEP_TIME : $SLEEP_TIME")
                */
                if(USER_ID.isNullOrBlank()){

                    if(MyApi.Logined_id.isNullOrBlank()){
                        return
                    }else{
                        USER_ID = MyApi.Logined_id
                    }
                }

                val retrofit = RetrofitClient.getInstance()
                val server = retrofit.create(InsertSleepInfoAPI::class.java)

                //API사용하여 통신
                server.getInsertSleepInfo(USER_ID, START_TIME, END_TIME, SLEEP_TIME).enqueue(object :
                    Callback<stateModel> {
                    override fun onResponse(
                        call: Call<stateModel>,
                        response: Response<stateModel>
                    ) {
                        Log.d(MyApi.TAG, "통신 성공(수면) : ${response.body()}")
                    }
                    override fun onFailure(call: Call<stateModel>, t: Throwable) {
                        Log.d(MyApi.TAG, "통신 실패(수면) : ${t.localizedMessage}")
                    }
                })
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
    }

    private suspend fun HeartRateInfo() {
        if (sdkStatus(this) == 3) {
            // Health Connect is available and installed.
            val healthConnectClient = HealthConnectClient.getOrCreate(this)
            var current1 = Instant.now()
            val current2 = Instant.now()
            current1 = current1.minusSeconds(604800)
            readHeartRateByTimeRange(healthConnectClient,current1,current2)
        } else {
            println("헬스커넥트 설치X")
        }
    }

    suspend fun readHeartRateByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val response =
                healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        HeartRateRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        dataOriginFilter = setOf(DataOrigin("com.sec.android.app.shealth")),
                        true,
                        1000,
                        null
                    )
                )
            for (HeartRate in response.records) {
                // Process each step record
                val cal1 = Calendar.getInstance()
                val df1: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                val date1:Date = df1.parse("197001010000")
                cal1.time = date1
                var plusMinute1:Int = ((HeartRate.samples[0].time.plusSeconds(60*60*9).epochSecond)/60).toInt()
                cal1.add(Calendar.MINUTE, plusMinute1)

                val auto = getSharedPreferences("autoLogin", Service.MODE_PRIVATE)
                var USER_ID = auto.getString("userId", null);
                val HEART_RATE = HeartRate.samples[0].beatsPerMinute
                val RATE_TIME = df1.format(cal1.time)

                if(USER_ID.isNullOrBlank()){

                    if(MyApi.Logined_id.isNullOrBlank()){
                        return
                    }else{
                        USER_ID = MyApi.Logined_id
                    }
                }

                val retrofit = RetrofitClient.getInstance()
                val server = retrofit.create(insertHeartRateInfoAPI::class.java)

                //API사용하여 통신
                server.getInsertHeartRateInfo(USER_ID, HEART_RATE, RATE_TIME).enqueue(object :
                    Callback<stateModel> {
                    override fun onResponse(
                        call: Call<stateModel>,
                        response: Response<stateModel>
                    ) {
                        Log.d(MyApi.TAG, "통신 성공(수면) : ${response.body()}")
                        //Log.d(MyApi.TAG, "USER_ID : $USER_ID, HEART_RATE : $HEART_RATE, RATE_TIME : $RATE_TIME")
                    }
                    override fun onFailure(call: Call<stateModel>, t: Throwable) {
                        Log.d(MyApi.TAG, "통신 실패(수면) : ${t.localizedMessage}")
                    }
                })
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        this.sensorManager?.unregisterListener(this)
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_LIGHT) {
                var date:Date = Date()
                val df: DateFormat = SimpleDateFormat("yyyyMMddHHmm")
                light = event.values[0].toLong()
                luxInfo(light,df.format(date))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private  fun luxInfo(LUX_VALUE : Long, LUX_TIME : String) {

        try {
             val auto = getSharedPreferences("autoLogin", Service.MODE_PRIVATE)
             var USER_ID = auto.getString("userId", null)
             
            if(USER_ID.isNullOrBlank()){

                if(MyApi.Logined_id.isNullOrBlank()){
                    return
                }else{
                    USER_ID = MyApi.Logined_id
                }
            }

            val retrofit = RetrofitClient.getInstance()
            val server = retrofit.create(insertLuxInfoAPI::class.java)

            //API사용하여 통신
            server.getInsertLuxInfo(USER_ID, LUX_VALUE, LUX_TIME).enqueue(object :
                Callback<stateModel> {
                override fun onResponse(
                    call: Call<stateModel>,
                    response: Response<stateModel>
                ) {
                    Log.d(MyApi.TAG, "통신 성공(조도) : ${response.body()}")
                Log.d(MyApi.TAG, "USER_ID : $USER_ID, LUX_VALUE : $LUX_VALUE, LUX_TIME : $LUX_TIME")
                }
                override fun onFailure(call: Call<stateModel>, t: Throwable) {
                    Log.d(MyApi.TAG, "통신 실패(조도) : ${t.localizedMessage}")
                } })
        
        } catch (e: Exception) {
            // Run error handling here.
        }
    }


    private fun dbInfo() {
        println("===============================================")
        println("===============================================")
        println("===============================================")
        println("===============================================")
        //외부 저장소 내 개별앱 공간에 저장하기
        val fileName: String = Date().getTime().toString() + ".mp3"
        var flag : Boolean = true
        outputPath =
            Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치
        recorder = MediaRecorder()
        recorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        recorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder?.setOutputFile(outputPath)
        try {
            recorder?.prepare() //초기화를 완료
        } catch (e: IOException) {
            return
        }

        System.out.println("데시벨 측정 시작")
        recorder?.start() //녹음기를 시작
        job = CoroutineScope(Dispatchers.Default).launch {
            while (flag) {
                delay(1000L) //1초에 한번씩 데시벨을 측정
                val amplitude = recorder!!.maxAmplitude
                val db = 20 * kotlin.math.log10(amplitude.toDouble()) //진폭 to 데시벨
                //데시벨은 기준 값을 기준으로 결정되는 것이라 한다.
                //그래서 기준값을 넣고싶다면 아래와 같이 기준값으로 나눠주면 된다.
                //val db = 20 * kotlin.math.log10(amplitude.toDouble()/기준값)
                //아무것도 안 넣는다면 우리가 흔히 생각하는 데시벨값이 된다. > https://www.joongang.co.kr/article/23615791#home
                if (db < 0) {
                    //진폭이 0 보다 크면 .. toDoSomething
                    //진폭이 0이하이면 데시벨이 -무한대로 나옵니다.
                }else{
                    flag = false
                    println("데시벨 : " + db + "\n")
                    println(outputPath)
                    val file = File(outputPath)
                    file.delete()
                    try {


                        val auto = getSharedPreferences("autoLogin", Service.MODE_PRIVATE)
                        var USER_ID = auto.getString("userId", null)
                        var DB_VALUE : Int = db.toInt()
                        var date:Date = Date()
                        val df: DateFormat = SimpleDateFormat("yyyyMMddHHmm")

                        val DB_TIME: String = df.format(date)

                        if(USER_ID.isNullOrBlank()){

                            if(MyApi.Logined_id.isNullOrBlank()){
                                return@launch
                            }else{
                                USER_ID = MyApi.Logined_id
                            }
                        }

                        val retrofit = RetrofitClient.getInstance()
                        val server = retrofit.create(insertDbInfoAPI::class.java)

                        //API사용하여 통신
                        server.getInsertDbInfo(USER_ID, DB_VALUE, DB_TIME).enqueue(object :
                            Callback<stateModel> {
                            override fun onResponse(
                                call: Call<stateModel>,
                                response: Response<stateModel>
                            ) {
                                Log.d(MyApi.TAG, "통신 성공(데시벨) : ${response.body()}")
                                Log.d(MyApi.TAG, "USER_ID : $USER_ID, DB_VALUE : $DB_VALUE, DB_TIME : $DB_TIME")
                            }
                            override fun onFailure(call: Call<stateModel>, t: Throwable) {
                                Log.d(MyApi.TAG, "통신 실패(데시벨) : ${t.localizedMessage}")
                            } })

                    } catch (e: Exception) {
                        // Run error handling here.
                    }
                }

            }
        }
    }
}