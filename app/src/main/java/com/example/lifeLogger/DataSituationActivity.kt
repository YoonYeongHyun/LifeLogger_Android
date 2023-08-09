package com.example.lifeLogger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class DataSituationActivity : AppCompatActivity() {

    var context: DataSituationActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_situation)
        val USER_ID = MyApi.Logined_id
        var date: Date = Date()
        val df: DateFormat = SimpleDateFormat("yyyyMMdd")

        val TIME: String = df.format(date)
        println(USER_ID)
        println(TIME)
    }
}