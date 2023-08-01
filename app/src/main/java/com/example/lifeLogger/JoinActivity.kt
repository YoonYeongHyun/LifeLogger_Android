package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeLogger.MyApi.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JoinActivity : AppCompatActivity() {

    var context: JoinActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("joinActivity")
        setContentView(R.layout.activity_join)

        val cancelButton = findViewById<Button>(R.id.cancel_button)

        cancelButton.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }


        val joinButton = findViewById<Button>(R.id.join_button)

        joinButton.setOnClickListener {
            val joinName:EditText = findViewById(R.id.join_name)
            val joinId:EditText = findViewById(R.id.join_id)
            val joinPassword:EditText = findViewById(R.id.join_password)
            val joinPwck:EditText = findViewById(R.id.join_pwck)

            //유효성 검사
            if(joinName.text.contentEquals("") ||joinId.text.contentEquals("") ||
                joinPassword.text.contentEquals("") ||joinPwck.text.contentEquals("")){
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            if(!joinPassword.text.contentEquals(joinPwck.text)){
                Toast.makeText(this, "비밀번호와 비밀번호확인이 다릅니다.", Toast.LENGTH_SHORT).show()
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
                    Log.d(TAG, "통신 성공 : ${response.body()}")

                    if(response.body()?.STATE.equals("0001")){
                        Toast.makeText(context, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
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