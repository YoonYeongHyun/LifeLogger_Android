package com.example.lifeLogger

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private var instance : Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    fun getInstance() : Retrofit {
        if(instance == null){
            instance = Retrofit.Builder()
                .baseUrl("http://20.200.213.94/Monitors/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return instance!!
    }

    fun getInstanceString() : Retrofit {
        if(instance == null){
            instance = Retrofit.Builder()
                .baseUrl("http://20.200.213.94/Monitors/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }
        return instance!!
    }
}