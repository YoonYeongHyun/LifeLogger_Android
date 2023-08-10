package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class DataSituationActivity : AppCompatActivity() {

    var context: DataSituationActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_situation)
        var date: Date = Date()
        val df: DateFormat = SimpleDateFormat("yyyyMMdd")

        val USER_ID = MyApi.Logined_id
        val DATE_TIME: String = df.format(date)
        println(USER_ID)
        println(DATE_TIME)

        val sleepStateView: TextView = findViewById(R.id.sleepStateView)
        val heartStateView: TextView = findViewById(R.id.heartStateView)
        val luxStateView: TextView = findViewById(R.id.luxStateView)
        val dbStateView: TextView = findViewById(R.id.dbStateView)

        val retrofit = RetrofitClient.getInstance()
        val server = retrofit.create(SelectDataSituationAPI::class.java)

        server.getSelectDataSituation(USER_ID, DATE_TIME).enqueue(object :
            Callback<dataSituationModel> {
            override fun onResponse(
                call: Call<dataSituationModel>,
                response: Response<dataSituationModel>
            ) {
                Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")

                if(response.body()?.STATE.equals("0001")){
                    if(response.body()?.SLEEP_COUNT.equals("0")){
                        sleepStateView.text = "누락"
                        sleepStateView.setBackgroundResource(R.drawable.rectangle_14_gray)
                    }
                    if(response.body()?.HEART_COUNT.equals("0")){
                        heartStateView.text = "누락"
                        heartStateView.setBackgroundResource(R.drawable.rectangle_14_gray)
                    }
                    if(response.body()?.LUX_COUNT.equals("0")){
                        luxStateView.text = "누락"
                        luxStateView.setBackgroundResource(R.drawable.rectangle_14_gray)
                    }
                    if(response.body()?.DB_COUNT.equals("0")){
                        dbStateView.text = "누락"
                        dbStateView.setBackgroundResource(R.drawable.rectangle_14_gray)
                    }
                }
            }
            override fun onFailure(call: Call<dataSituationModel>, t: Throwable) {
                Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
            }
        })
    }
}