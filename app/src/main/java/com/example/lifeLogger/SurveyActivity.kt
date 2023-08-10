package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurveyActivity : AppCompatActivity()  {

    var context: SurveyActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        val preIntent = intent
        val USER_ID = MyApi.Logined_id
        val USER_NAME = MyApi.Logined_name
        val SURVEY_SEQUENCE = preIntent.getStringExtra("times")


        var sub_title = findViewById<(TextView)>(R.id.sub_title)
        sub_title.text = USER_NAME + "님 " +  SURVEY_SEQUENCE + "차 설문조사"

        var radioFlag1 = false
        var radioFlag2 = false
        var radioFlag3 = false
        var radioFlag4 = false
        var radioFlag5 = false
        var radioFlag6 = false
        var radioFlag7 = false

        val Rgroup1 : RadioGroup = findViewById(R.id.Rgroup1)
        val Rgroup2 : RadioGroup = findViewById(R.id.Rgroup2)
        val Rgroup3 : RadioGroup = findViewById(R.id.Rgroup3)
        val Rgroup4 : RadioGroup = findViewById(R.id.Rgroup4)
        val Rgroup5 : RadioGroup = findViewById(R.id.Rgroup5)
        val Rgroup6 : RadioGroup = findViewById(R.id.Rgroup6)
        val Rgroup7 : RadioGroup = findViewById(R.id.Rgroup7)

        var radioValue1 = ""
        var radioValue2 = ""
        var radioValue3 = ""
        var radioValue4 = ""
        var radioValue5 = ""
        var radioValue6 = ""
        var radioValue7 = ""

        Rgroup1.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio1_1   -> radioValue1 = "1"
                R.id.radio1_2   -> radioValue1 = "2"
                R.id.radio1_3   -> radioValue1 = "3"
                R.id.radio1_4   -> radioValue1 = "4"
                R.id.radio1_5   -> radioValue1 = "5"
            }
            radioFlag1 = true
        }
        Rgroup2.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio2_1   -> radioValue2 = "1"
                R.id.radio2_2   -> radioValue2 = "2"
                R.id.radio2_3   -> radioValue2 = "3"
                R.id.radio2_4   -> radioValue2 = "4"
                R.id.radio2_5   -> radioValue2 = "5"
            }
            radioFlag2 = true
        }
        Rgroup3.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio3_1   -> radioValue3 = "1"
                R.id.radio3_2   -> radioValue3 = "2"
                R.id.radio3_3   -> radioValue3 = "3"
                R.id.radio3_4   -> radioValue3 = "4"
                R.id.radio3_5   -> radioValue3 = "5"
            }
            radioFlag3 = true
        }
        Rgroup4.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio4_1   -> radioValue4 = "1"
                R.id.radio4_2   -> radioValue4 = "2"
                R.id.radio4_3   -> radioValue4 = "3"
                R.id.radio4_4   -> radioValue4 = "4"
                R.id.radio4_5   -> radioValue4 = "5"
            }
            radioFlag4 = true
        }
        Rgroup5.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio5_1   -> radioValue5 = "1"
                R.id.radio5_2   -> radioValue5 = "2"
                R.id.radio5_3   -> radioValue5 = "3"
                R.id.radio5_4   -> radioValue5 = "4"
                R.id.radio5_5   -> radioValue5 = "5"
            }
            radioFlag5 = true
        }
        Rgroup6.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio6_1   -> radioValue6 = "1"
                R.id.radio6_2   -> radioValue6 = "2"
                R.id.radio6_3   -> radioValue6 = "3"
                R.id.radio6_4   -> radioValue6 = "4"
                R.id.radio6_5   -> radioValue6 = "5"
            }
            radioFlag6 = true
        }
        Rgroup7.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.radio7_1   -> radioValue7 = "1"
                R.id.radio7_2   -> radioValue7 = "2"
                R.id.radio7_3   -> radioValue7 = "3"
                R.id.radio7_4   -> radioValue7 = "4"
                R.id.radio7_5   -> radioValue7 = "5"
            }
            radioFlag7 = true
        }

        var submitButton = findViewById<(Button)>(R.id.submitButton)

        submitButton.setOnClickListener{
            if(radioFlag1 && radioFlag2 && radioFlag3 && radioFlag4 && radioFlag5 && radioFlag6 && radioFlag7){

                val QUESTION_RESULT_1 = radioValue1
                val QUESTION_RESULT_2 = radioValue2
                val QUESTION_RESULT_3 = radioValue3
                val QUESTION_RESULT_4 = radioValue4
                val QUESTION_RESULT_5 = radioValue5
                val QUESTION_RESULT_6 = radioValue6
                val QUESTION_RESULT_7 = radioValue7

                val retrofit = RetrofitClient.getInstance()
                val server = retrofit.create(InsertSurveyInfoAPI::class.java)

                //API사용하여 통신
                server.getInsertSurveyInfo(USER_ID, SURVEY_SEQUENCE, QUESTION_RESULT_1, QUESTION_RESULT_2, QUESTION_RESULT_3, QUESTION_RESULT_4
                , QUESTION_RESULT_5, QUESTION_RESULT_6, QUESTION_RESULT_7).enqueue(object :
                    Callback<stateModel> {
                    override fun onResponse(
                        call: Call<stateModel>,
                        response: Response<stateModel>
                    ) {
                        Log.d(MyApi.TAG, "통신 성공(설문) : ${response.body()}")
                        val surveyListIntent = Intent(applicationContext, MainActivity::class.java)
                        surveyListIntent.putExtra("Direction","SurveyList")
                        startActivity(surveyListIntent)
                    }
                    override fun onFailure(call: Call<stateModel>, t: Throwable) {
                        Log.d(MyApi.TAG, "통신 실패(설문) : ${t.localizedMessage}")
                    }
                })
            }else{
                Toast.makeText(context, "모든 질문에 대한 답을 선택하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

}