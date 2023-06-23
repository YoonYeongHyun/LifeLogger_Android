package com.example.biocheck

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
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.platform.client.permission.Permission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    var context: LoginActivity = this

    //뒤로 가기 키 관련 변수
    private var backKeyPressedTime : Long = 0
    private var terminationTime : Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //헬스커넥터 권한

        val requestPermission =
            this.registerForActivityResult(
                PermissionController.createRequestPermissionResultContract()
            ) { grantedPermissions: Set<String> ->
                if (
                    grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class))
                ) {
                    println("uuuuuuuuuuuuuuuuuuuuuu")
                    println(grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class)))
                    // Read or process steps related health records.
                } else {
                    println("dddddddddddddddddddd")
                    println(grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class)))

                    // user denied permission
                }
            }

        requestPermission.launch(setOf(HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(SpeedRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(SleepStageRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ElevationGainedRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
        ))


        /*
        val permissionController :PermissionController
        val job1 = CoroutineScope(Dispatchers.Main).launch {
            val grantedPermissions = PermissionController.getGrantedPermissions()
            if (grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class))) {
                // Read or process steps related health records.
            } else {
                // user denied permission
            }
        }
        */





        //자동 로그인 동작

        val loginId: EditText = findViewById(R.id.login_id)
        val loginPassword: EditText = findViewById(R.id.login_password)
        val autoCheck: CheckBox = findViewById(R.id.auto_check)

        val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
        val userId = auto.getString("userId", null);
        val passwordNo = auto.getString("passwordNo", null);


        if(userId != null && passwordNo != null){
            autoCheck.isChecked = true
            loginId.setText(userId)
            loginPassword.setText(passwordNo)

        }

        //수집 관련 퍼미션 확인
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
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            println("권한 없음")
            ActivityCompat.requestPermissions(
                this@LoginActivity,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                    Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.RECEIVE_MMS,

                ),
                1
            )
        }else{
            if(userId != null && passwordNo != null) {
                //loginFunction()
            }
        }
        if (!checkPermission()) {
            val permissionIntent = Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse(
                    "package:$packageName"
                )
            )
            startActivity(permissionIntent)
        }

        //가입 버튼 클릭시 이동
        val joinButton = findViewById<Button>(R.id.join_button)

        joinButton.setOnClickListener {
            val intent = Intent(applicationContext, JoinActivity::class.java)
            intent.putExtra("message", "액티비티가 이동됐다!")
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

        val retrofit = RetrofitClient.getInstance()
        val server = retrofit.create(LoginUserAPI::class.java)

        //API사용하여 통신
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
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    MyApi.Logined_id = USER_ID;
                    startActivity(intent)
                }else{
                    Toast.makeText(context, "${response.body()?.MESSAGE}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<userModel>, t: Throwable) {
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
