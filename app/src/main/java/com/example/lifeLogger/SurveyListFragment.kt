package com.example.lifeLogger

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SurveyListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    var survey_button_first = view?.findViewById<(Button)>(R.id.survey_button_first)
    var survey_button_second = view?.findViewById<(Button)>(R.id.survey_button_second)
    var survey_button_third = view?.findViewById<(Button)>(R.id.survey_button_third)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val USER_ID= MyApi.Logined_id

        val retrofit = RetrofitClient.getInstance()
        val server = retrofit.create(SelectSurveyListAPI::class.java)
        println(USER_ID)
        //API사용하여 통신
        server.getSelectSurveyList(USER_ID).enqueue(object :
            Callback<surveyModel> {
            override fun onResponse(
                call: Call<surveyModel>,
                response: Response<surveyModel>
            ) {
                Log.d(MyApi.TAG, "통신 성공 : ${response.body()}")
                val JOIN_TIME = response.body()?.JOIN_TIME
                val SECOND_SURVEY_DATE = response.body()?.SECOND_SURVEY_DATE
                val THIRD_SURVEY_DATE = response.body()?.THIRD_SURVEY_DATE
                val FIRST_SURVEY_FLAG = response.body()?.FIRST_SURVEY_FLAG
                val SECOND_SURVEY_FLAG = response.body()?.SECOND_SURVEY_FLAG
                val THIRD_SURVEY_FLAG = response.body()?.THIRD_SURVEY_FLAG

                var text_survey_subtitle_first = view?.findViewById<TextView>(R.id.text_survey_subtitle_first)
                var text_survey_subtitle_second = view?.findViewById<TextView>(R.id.text_survey_subtitle_second)
                var text_survey_subtitle_third = view?.findViewById<TextView>(R.id.text_survey_subtitle_third)


                survey_button_first = view?.findViewById(R.id.survey_button_first)
                survey_button_second = view?.findViewById(R.id.survey_button_second)
                survey_button_third = view?.findViewById(R.id.survey_button_third)
                
                if (text_survey_subtitle_first != null) {
                    text_survey_subtitle_first.text = "활성화 날짜 : $JOIN_TIME"
                }

                if (text_survey_subtitle_second != null) {
                    text_survey_subtitle_second.text = "활성화 날짜 : $SECOND_SURVEY_DATE"
                }

                if (text_survey_subtitle_third != null) {
                    text_survey_subtitle_third.text = "활성화 날짜 : $THIRD_SURVEY_DATE"
                }



                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val long_now = System.currentTimeMillis()
                val toDate : Date =  Date(long_now)
                val firstDate: Date = sdf.parse(JOIN_TIME)
                val secondDate: Date = sdf.parse(SECOND_SURVEY_DATE)
                val thirdDate: Date = sdf.parse(THIRD_SURVEY_DATE)
                println("1 - $firstDate")
                println("2 - $secondDate")
                println("3 - $thirdDate")

                val cmp_second = toDate.compareTo(secondDate)


                if(FIRST_SURVEY_FLAG =="T"){
                    if (survey_button_first != null) {
                        survey_button_first!!.text = "완료"
                        survey_button_first!!.setBackgroundResource(R.drawable.rectangle_14_gray)
                        survey_button_first!!.isClickable = false
                    }
                }

                if(SECOND_SURVEY_FLAG =="T"){
                    if (survey_button_second != null) {
                        survey_button_second!!.text = "완료"
                        survey_button_second!!.setBackgroundResource(R.drawable.rectangle_14_gray)
                        survey_button_second!!.isClickable = false
                    }
                }else{
                    when {
                        cmp_second >= 0 -> {
                            println("$toDate >= $secondDate")

                        }
                        cmp_second < 0 -> {
                            println("$toDate < $secondDate")

                            if (survey_button_second != null) {
                                survey_button_second!!.text = "예정"
                                survey_button_second!!.setBackgroundResource(R.drawable.rectangle_14_gray)
                                survey_button_second!!.isEnabled = false
                            }
                        }
                    }
                }



                val cmp_third = toDate.compareTo(thirdDate)

                if(THIRD_SURVEY_FLAG =="T"){
                    if (survey_button_third != null) {
                        survey_button_third!!.text = "완료"
                        survey_button_third!!.setBackgroundResource(R.drawable.rectangle_14_gray)
                        survey_button_third!!.isEnabled = false
                    }
                }else{
                    when {
                        cmp_third >= 0 -> {
                            println("$toDate >= $thirdDate")

                        }
                        cmp_third < 0 -> {
                            println("$toDate < $thirdDate")

                            if (survey_button_third != null) {
                                survey_button_third!!.text = "예정"
                                survey_button_third!!.setBackgroundResource(R.drawable.rectangle_14_gray)
                                survey_button_third!!.isEnabled = false
                            }
                        }
                    }
                }

                survey_button_first?.setOnClickListener{
                    println("클릭")
                    val surveyIntent = Intent(activity, SurveyActivity::class.java)
                    surveyIntent.putExtra("times","1")
                    startActivity(surveyIntent)
                }
                survey_button_second?.setOnClickListener{
                    println("클릭")
                    val surveyIntent = Intent(activity, SurveyActivity::class.java)
                    surveyIntent.putExtra("times","2")
                    startActivity(surveyIntent)
                }
                survey_button_third?.setOnClickListener{
                    println("클릭")
                    val surveyIntent = Intent(activity, SurveyActivity::class.java)
                    surveyIntent.putExtra("times","3")
                    startActivity(surveyIntent)
                }
            }
            override fun onFailure(call: Call<surveyModel>, t: Throwable) {
                Log.d(MyApi.TAG, "통신 실패 : ${t.localizedMessage}")
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SurveyListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}