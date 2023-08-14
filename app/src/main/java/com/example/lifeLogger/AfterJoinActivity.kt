package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AfterJoinActivity: AppCompatActivity() {

    private lateinit var USER_ID : String
    private lateinit var USER_NAME : String
    private lateinit var USER_PWD : String
    var context: AfterJoinActivity = this

    //뒤로 가기 키 관련 변수
    private var backKeyPressedTime : Long = 0
    private var terminationTime : Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_join)

        val preIntent = intent
        USER_ID = preIntent.getStringExtra("USER_ID").toString()
        USER_NAME = preIntent.getStringExtra("USER_NAME").toString()
        USER_PWD = preIntent.getStringExtra("USER_PWD").toString()
        println(preIntent.getStringExtra("USER_ID"))
        println(preIntent.getStringExtra("USER_NAME"))
        println(preIntent.getStringExtra("USER_PWD"))

        var USER_GENDER : String = ""
        
        val spinner1 : Spinner = findViewById(R.id.spinner_1)
        val spinner2 : Spinner = findViewById(R.id.spinner_2)
        val spinner3 : Spinner = findViewById(R.id.spinner_3)

        val gender_radio : RadioGroup = findViewById(R.id.gender_radio)
        spinner1.adapter = ArrayAdapter.createFromResource(context, R.array.arraySurveyValues1, android.R.layout.simple_spinner_item)
        spinner2.adapter = ArrayAdapter.createFromResource(context, R.array.arraySurveyValues2, android.R.layout.simple_spinner_item)
        spinner3.adapter = ArrayAdapter.createFromResource(context, R.array.arraySurveyValues3, android.R.layout.simple_spinner_item)

        gender_radio.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio_male   -> USER_GENDER = "남성"
                R.id.radio_female -> USER_GENDER = "여성"
            }
        }

        val save_button = findViewById<Button>(R.id.save_button)

        save_button.setOnClickListener {
            if(USER_GENDER == ""){
                Toast.makeText(this, "성별을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var USER_AGE = spinner1.selectedItem.toString()
            var USER_JOB = spinner2.selectedItem.toString()
            var USER_FAMILY_CNT = spinner3.selectedItem.toString()
            if(USER_AGE == "선택(필수)" || USER_JOB == "선택(필수)" || USER_FAMILY_CNT == "선택(필수)" ){

                Toast.makeText(this, "모든 값을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //레트로핏 & 서버설정변수 선언
            val retrofit = RetrofitClient.getInstance()
            val server = retrofit.create(InsertUserInfoAPI::class.java)
            println(USER_ID)
            println(USER_NAME)
            println(USER_PWD)
            //API사용하여 통신
            //call, response 콜백 모델(stateModel, userModel) 잘보고 설정
            server.getInsertUserInfo(USER_ID, USER_NAME, USER_PWD, USER_AGE, USER_GENDER, USER_JOB, USER_FAMILY_CNT).enqueue(object :
                Callback<stateModel> {
                override fun onResponse(
                    call: Call<stateModel>,
                    response: Response<stateModel>
                ) {
                    Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")

                    if(response.body()?.STATE.equals("0001")){
                        Toast.makeText(context, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                        val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(loginIntent)
                    }else{
                        Toast.makeText(context, "${response.body()?.MESSAGE}", Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onFailure(call: Call<stateModel>, t: Throwable) {
                    Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
                }
            })
        }
    }


    //첫 로그인 화면 뒤로 가기 금지 및 두번 입력 시 종료
    override fun onBackPressed() {

        if(System.currentTimeMillis() > backKeyPressedTime + terminationTime){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "페이지를 나가시면 회원가입이 취소됩니다.", Toast.LENGTH_SHORT).show()
            return
        }else if(System.currentTimeMillis() <= backKeyPressedTime + terminationTime){
            val loginIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}