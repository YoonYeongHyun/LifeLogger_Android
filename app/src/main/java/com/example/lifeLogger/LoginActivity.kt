package com.example.lifeLogger

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class LoginActivity : AppCompatActivity() {

    var context: LoginActivity = this

    //취소 키 관련 변수
    //terminationTime = 2500 - 2.5초 딜레이
    private var backKeyPressedTime : Long = 0
    private var terminationTime : Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //헬스커넥트 삼성헬스 앱설치유무에 따라 설치 창 출력
        val HealthConnect = "com.google.android.apps.healthdata"
        val SamsungHealth = "com.sec.android.app.shealth"
        val installApp1 = packageManager.getLaunchIntentForPackage(HealthConnect)
        val installApp2 = packageManager.getLaunchIntentForPackage(SamsungHealth)
        if (installApp1 == null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = (Uri.parse("market://details?id=$HealthConnect"))
            startActivity(intent)
        }
        if (installApp2 == null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = (Uri.parse("market://details?id=$SamsungHealth"))
            startActivity(intent)
        }

        //헬스커넥터 삼성헬스 - 연결 권한 부여
        val requestPermission =
            this.registerForActivityResult(
                PermissionController.createRequestPermissionResultContract()
            ) { grantedPermissions: Set<String> ->
                if (
                    grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class))
                ) {
                    println(grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class)))
                } else {
                    println(grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class)))
                }
            }

        //모바일 앱 권한요청(추후에 안쓰는 권한 삭제 필요)
        requestPermission.launch(setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(SpeedRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(SleepStageRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ElevationGainedRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
        ))

        //ui요소 객체 생성 (R.id.해당아이디)
        val loginId: EditText = findViewById(R.id.login_id)
        val loginPassword: EditText = findViewById(R.id.login_password)
        val autoCheck: CheckBox = findViewById(R.id.auto_check)

        //아이디 비밀번호 자동 입력 & 로그인
        //getSharedPreferences 일종의 앱에서 쓰는 쿠기 같은 기능
        //객체 생성뒤 게터 세터로 사용
        val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
        val userId = auto.getString("userId", null);
        val passwordNo = auto.getString("passwordNo", null);

        if(userId != null && passwordNo != null){
            autoCheck.isChecked = true
            loginId.setText(userId)
            loginPassword.setText(passwordNo)
            loginFunction()
        }

        //앱 사용 권한 체크 (나중에 안 쓸거는 제거 해야됨)
        if (ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.RECEIVE_MMS
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.ACCESS_MEDIA_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@LoginActivity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@LoginActivity,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                    Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_MMS,Manifest.permission.READ_MEDIA_IMAGES,
                ),
                1
            )
        }

        if (!checkPermission()) {
            val permissionIntent = Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse(
                    "package:$packageName"
                )
            )
            startActivity(permissionIntent)
        }

        //가입 버튼 클릭시 레이아웃 이동
        val joinButton = findViewById<Button>(R.id.join_button)

        joinButton.setOnClickListener {
            val intent = Intent(applicationContext, JoinActivity::class.java)
            startActivity(intent)
        }


        //로그인 버튼 클릭 시 로그인
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            loginFunction()
        }
    }



    private fun loginFunction() {

        val loginId: EditText = findViewById(R.id.login_id)
        val loginPassword: EditText = findViewById(R.id.login_password)
        val autoCheck: CheckBox = findViewById(R.id.auto_check)

        val USER_ID = loginId.text.toString()
        val USER_PWD = loginPassword.text.toString()
        val AUTO_CHECK = autoCheck.isChecked;

        //레트로핏 & 서버설정변수 선언
        val retrofit = RetrofitClient.getInstance()
        //create먕량어 변수는 APIS.kt에서 확인
        val server = retrofit.create(LoginUserAPI::class.java)

        //API사용하여 통신
        //call, response 콜백 모델(stateModel, userModel 등등) 잘보고 설정
        // 콜백 모델들은 PostModel.kt 참고
        //콜백 모델과 php파일 통신 변수명 일치하지 않으면 오류
        server.getLoginUser(USER_ID, USER_PWD).enqueue(object :
            Callback<userModel> {
            override fun onResponse(
                call: Call<userModel>,
                response: Response<userModel>
            ) {
                Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")

                if(response.body()?.STATE.equals("0001")){
                    if (AUTO_CHECK) {
                        // 자동 로그인 데이터 저장
                        val auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
                        val autoLoginEdit = auto.edit()
                        autoLoginEdit.putString("userId", USER_ID)
                        autoLoginEdit.putString("passwordNo", USER_PWD)
                        autoLoginEdit.commit()
                    }
                    Toast.makeText(context, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    MyApi.Logined_id = response.body()?.USER_ID.toString()
                    MyApi.Logined_name = response.body()?.USER_NAME.toString()

                    //오늘의 상태 입력여부 확인
                    checkTodayState()

                }else{
                    Toast.makeText(context, "${response.body()?.MESSAGE}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<userModel>, t: Throwable) {
                Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
            }
        })
    }

    //오늘의 정신 상태 입력 여부 확인 
    private fun checkTodayState(){

        print(MyApi.Logined_id)

        var date: Date = Date()
        val df: DateFormat = SimpleDateFormat("yyyyMMdd")

        val USER_ID = MyApi.Logined_id
        val STATE_DATE: String = df.format(date)
        Log.d(MyApi.TAG, "USER_ID $USER_ID")
        Log.d(MyApi.TAG, "STATE_DATE $STATE_DATE")

        val retrofit = RetrofitClient.getInstance()
        val server = retrofit.create(SelectTodayStateAPI::class.java)

        server.getSelectTodayState(USER_ID, STATE_DATE).enqueue(object :
            Callback<stateModel> {
            override fun onResponse(
                call: Call<stateModel>,
                response: Response<stateModel>
            ) {
                Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")

                if(response.body()?.STATE.equals("0001")){
                    println(response.body()?.MESSAGE)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    val intent = Intent(applicationContext, TodayStateActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<stateModel>, t: Throwable) {
                Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
            }
        })

    }
    // 패키지 사용상태 확인 권한 체크
    private fun checkPermission(): Boolean {
        var granted = false
        val appOps = applicationContext
            .getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), applicationContext.packageName
        )
        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = applicationContext.checkCallingOrSelfPermission(
                Manifest.permission.PACKAGE_USAGE_STATS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            granted = mode == AppOpsManager.MODE_ALLOWED
        }
        return granted
    }

    //첫 로그인 화면 뒤로 가기 금지 및 두번 입력 시 종료
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
