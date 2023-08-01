package com.example.lifeLogger

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class TodayStateActivity : AppCompatActivity() {
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_state)

        val textView1:TextView = findViewById(R.id.textView1)
        val seekBar:SeekBar = findViewById(R.id.seekBar)


        var seekBarValue : Int = 3;
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
    }

    private fun change_emo(seekBarValue: Int) {

        val textView2:TextView = findViewById(R.id.textView2)

        val imageVbad:ImageView = findViewById(R.id.imageVbad)
        val imageBad:ImageView = findViewById(R.id.imageBad)
        val imageNormal:ImageView = findViewById(R.id.imageNormal)
        val imageGood:ImageView = findViewById(R.id.imageGood)
        val imageVgood:ImageView = findViewById(R.id.imageVgood)

        imageVbad.setImageResource(R.drawable.vbad);
        imageBad.setImageResource(R.drawable.bad);
        imageNormal.setImageResource(R.drawable.normal);
        imageGood.setImageResource(R.drawable.good);
        imageVgood.setImageResource(R.drawable.vgood);

        when (seekBarValue) {
            1 -> {
                imageVbad.setImageResource(R.drawable.vbad_checked);
                textView2.text = "매우 나쁨";
            }
            2 -> {
                imageBad.setImageResource(R.drawable.bad_checked);
                textView2.text = "나쁨";
            }
            3 -> {
                imageNormal.setImageResource(R.drawable.normal_checked);
                textView2.text = "보통";
            }
            4 -> {
                imageGood.setImageResource(R.drawable.good_checked);
                textView2.text = "좋음";
            }
            5 -> {
                imageVgood.setImageResource(R.drawable.vgood_checked);
                textView2.text = "매우 좋음";
            }
            else -> println("null")
        }
    }


}