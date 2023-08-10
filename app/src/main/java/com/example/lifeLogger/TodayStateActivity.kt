package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class TodayStateActivity : AppCompatActivity() {
    val context = this
    var seekBarValue : Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_state)

        var nameTitleText = findViewById<TextView>(R.id.nameTitleText)
        nameTitleText.text = MyApi.Logined_name + "님"

        val seekBar:SeekBar = findViewById(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean) {
                seekBarValue = p2 + 1
                context.change_emo(seekBarValue)
                Log.d("SeekbarLog", "changed $p2")
            }

            override fun onStartTrackingTouch(p1: SeekBar) {
                Log.d("SeekbarLog", "터치 시작 $p1")
            }

            override fun onStopTrackingTouch(p1: SeekBar) {
                Log.d("SeekbarLog", "터치 끝 $p1")
            }
        })

        val saveButton : Button  = findViewById(R.id.saveButton)

        saveButton.setOnClickListener{
            save_state()
        }


    }


    private fun change_emo(seekBarValue: Int) {

        val textView2:TextView = findViewById(R.id.textView2)

        val imageVbad:ImageView = findViewById(R.id.imageVbad)
        val imageBad:ImageView = findViewById(R.id.imageBad)
        val imageNormal:ImageView = findViewById(R.id.imageNormal)
        val imageGood:ImageView = findViewById(R.id.imageGood)
        val imageVgood:ImageView = findViewById(R.id.imageVgood)

        imageVbad.setImageResource(R.drawable.vbad)
        imageBad.setImageResource(R.drawable.bad)
        imageNormal.setImageResource(R.drawable.normal)
        imageGood.setImageResource(R.drawable.good)
        imageVgood.setImageResource(R.drawable.vgood)

        when (seekBarValue) {
            1 -> {
                imageVbad.setImageResource(R.drawable.vbad_checked)
                textView2.text = "매우 나쁨"
            }
            2 -> {
                imageBad.setImageResource(R.drawable.bad_checked)
                textView2.text = "나쁨"
            }
            3 -> {
                imageNormal.setImageResource(R.drawable.normal_checked)
                textView2.text = "보통"
            }
            4 -> {
                imageGood.setImageResource(R.drawable.good_checked)
                textView2.text = "좋음"
            }
            5 -> {
                imageVgood.setImageResource(R.drawable.vgood_checked)
                textView2.text = "매우 좋음"
            }
            else -> println("null")
        }
    }

    private fun save_state() {


        val date = Date()
        val df: DateFormat = SimpleDateFormat("yyyyMMdd")

        val USER_ID = MyApi.Logined_id
        val STATE_SCORE: Int = seekBarValue
        val STATE_DATE: String = df.format(date)

        println(USER_ID)
        println(STATE_SCORE)
        println(STATE_DATE)

        val retrofit = RetrofitClient.getInstance()
        val server = retrofit.create(InsertTodayStateInfoAPI::class.java)

        //API사용하여 통신
        server.getInsertTodayStateInfo(USER_ID, STATE_SCORE, STATE_DATE).enqueue(object :
            Callback<stateModel> {
            override fun onResponse(
                call: Call<stateModel>,
                response: Response<stateModel>
            ) {
                Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")
                Log.d(MyApi.TAG, "통신 성공 : ${response}")
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
            override fun onFailure(call: Call<stateModel>, t: Throwable) {
                Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
            }
        })
    }

}