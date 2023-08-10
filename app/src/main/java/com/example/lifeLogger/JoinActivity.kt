package com.example.lifeLogger

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeLogger.MyApi.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JoinActivity : AppCompatActivity() {

    var context: JoinActivity = this
    var passwordFlag : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("joinActivity")
        setContentView(R.layout.activity_join)

        val joinButton = findViewById<Button>(R.id.join_button)
        val joinPassword:EditText = findViewById(R.id.join_password)
        val cautionText:TextView = findViewById(R.id.cautionText)

        joinPassword.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val pattern1 = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,20}$"
                if (s != null && s.length <= 16 && s.length >=8) {
                    cautionText.setTextColor(Color.parseColor("#FFFFFF"))
                    passwordFlag = true
                }else{
                    cautionText.setTextColor(Color.parseColor("#CC3333"))
                    passwordFlag = false
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        } )




        joinButton.setOnClickListener {
            val joinName:EditText = findViewById(R.id.join_name)
            val joinId:EditText = findViewById(R.id.join_id)
            val joinPwck:EditText = findViewById(R.id.join_pwck)

            //유효성 검사 항목 나중에 추가 필요 (길이, 정규형 등등)
            if(joinName.text.contentEquals("") ||joinId.text.contentEquals("") ||
                joinPassword.text.contentEquals("") ||joinPwck.text.contentEquals("")){
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            if(!joinPassword.text.contentEquals(joinPwck.text)){
                Toast.makeText(this, "비밀번호와 비밀번호확인이 다릅니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            if(!passwordFlag){
                Toast.makeText(this, "비밀번호는 8~ 16자 이하로 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }
            val USER_ID = joinId.text.toString()
            val USER_PWD = joinPassword.text.toString()
            val USER_NAME = joinName.text.toString()

            val retrofit = RetrofitClient.getInstance()
            val server = retrofit.create(InsertUserAPI::class.java)

            //API사용하여 통신
            server.getInsertUser(USER_ID, USER_PWD, USER_NAME).enqueue(object : Callback<userModel> {
                override fun onResponse(
                    call: Call<userModel>,
                    response: Response<userModel>
                ) {
                    Log.d(TAG, "통신 성공 : ${response}")

                    println(USER_ID)
                    println(USER_PWD)
                    println(USER_NAME)
                    if(response.body()?.STATE.equals("0001")){
                        Toast.makeText(context, "다음 항목들을 모두 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        val afterJoinIntent = Intent(applicationContext, AfterJoinActivity::class.java)
                        afterJoinIntent.putExtra("USER_NAME",USER_NAME)
                        afterJoinIntent.putExtra("USER_ID",USER_ID)
                        afterJoinIntent.putExtra("USER_PWD",USER_PWD)
                        startActivity(afterJoinIntent)
                    }else{
                        Toast.makeText(context, "${response.body()?.MESSAGE}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<userModel>, t: Throwable) {
                    Log.d(TAG, "통신 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}