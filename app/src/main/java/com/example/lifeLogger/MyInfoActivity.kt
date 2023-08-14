package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyInfoActivity: AppCompatActivity()  {

    var context: MyInfoActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        val USER_ID = MyApi.Logined_id
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

        val retrofit = RetrofitClient.getInstance()
        val server = retrofit.create(SelectMyInfoAPI::class.java)

        server.getSelectMyInfo(USER_ID).enqueue(object :
            Callback<myInfoModel> {
            override fun onResponse(
                call: Call<myInfoModel>,
                response: Response<myInfoModel>
            ) {
                Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")
                var USER_GENDER = response.body()?.USER_GENDER.toString()
                var USER_AGE = response.body()?.USER_AGE.toString()
                var USER_JOB = response.body()?.USER_JOB.toString()
                var USER_FAMILY_CNT = response.body()?.USER_FAMILY_CNT.toString()

                var radio_male: RadioButton = findViewById(R.id.radio_male)
                var radio_female: RadioButton = findViewById(R.id.radio_female)

                 when (USER_GENDER) {
                     "남성" -> radio_male.isChecked = true
                     "여성" -> radio_female.isChecked = true
                 }

                when (USER_AGE) {
                    "10대"  -> spinner1.setSelection(1)
                    "20대"  -> spinner1.setSelection(2)
                    "30대"  -> spinner1.setSelection(3)
                    "40대"  -> spinner1.setSelection(4)
                    "50대"  -> spinner1.setSelection(5)
                }

                when (USER_JOB) {
                    "무직"      -> spinner2.setSelection(1)
                    "학생"      -> spinner2.setSelection(2)
                    "아르바이트" -> spinner2.setSelection(3)
                    "자영업"    -> spinner2.setSelection(4)
                    "건설업"    -> spinner2.setSelection(5)
                    "서비스업"  -> spinner2.setSelection(6)
                    "제조업"    -> spinner2.setSelection(7)
                    "IT"       -> spinner2.setSelection(8)
                }

                when (USER_FAMILY_CNT) {
                    "1명"     -> spinner3.setSelection(1)
                    "2명"     -> spinner3.setSelection(2)
                    "3명"     -> spinner3.setSelection(3)
                    "4명"     -> spinner3.setSelection(4)
                    "5명 이상" -> spinner3.setSelection(5)
                }

            }
            override fun onFailure(call: Call<myInfoModel>, t: Throwable) {
                Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
            }
        })


        val save_button = findViewById<Button>(R.id.save_button)

        save_button.setOnClickListener {
            if (USER_GENDER == "") {
                Toast.makeText(this, "성별을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var USER_AGE = spinner1.selectedItem.toString()
            var USER_JOB = spinner2.selectedItem.toString()
            var USER_FAMILY_CNT = spinner3.selectedItem.toString()
            if (USER_AGE == "선택(필수)" || USER_JOB == "선택(필수)" || USER_FAMILY_CNT == "선택(필수)" ) {

                Toast.makeText(this, "모든 값을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //레트로핏 & 서버설정변수 선언
            val retrofit = RetrofitClient.getInstance()
            val server = retrofit.create(UpdateUserInfoAPI::class.java)
            println(USER_ID)
            //API사용하여 통신
            //call, response 콜백 모델(stateModel, userModel) 잘보고 설정
            server.getUpdateUserInfo(USER_ID, USER_AGE, USER_GENDER, USER_JOB, USER_FAMILY_CNT).enqueue(object :
                Callback<stateModel> {
                override fun onResponse(
                    call: Call<stateModel>,
                    response: Response<stateModel>
                ) {
                    Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")

                    if (response.body()?.STATE.equals("0001")) {
                        Toast.makeText(context, "내 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        val configIntent = Intent(applicationContext, MainActivity::class.java)
                        configIntent.putExtra("Direction","Config")
                        startActivity(configIntent)
                    } else {
                        Toast.makeText(context, "${response.body()?.MESSAGE}", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                override fun onFailure(call: Call<stateModel>, t: Throwable) {
                    Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}