package com.example.biocheck

import io.reactivex.internal.operators.flowable.FlowableTakeLastOne
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface InsertUserAPI {
    @FormUrlEncoded
    @POST(MyApi.insertUser)
    fun getInsertUser(
        @Field("USER_ID") USER_ID: String?,
        @Field("USER_PWD") USER_PWD: String,
        @Field("USER_NAME") USER_NAME: String
    ): Call<userModel>
}

interface LoginUserAPI {
    @FormUrlEncoded
    @POST(MyApi.loginUser)
    fun getLoginUser(
        @Field("USER_ID") USER_ID: String?,
        @Field("USER_PWD") USER_PWD: String
    ): Call<userModel>
}

interface InsertCallInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertCallInfo)
    fun getInsertCallInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("CALL_ID") CALL_ID: String,
        @Field("CALL_DURATION") CALL_DURATION: Int,
        @Field("CALL_DATE") CALL_DATE: String,
        @Field("CALL_TYPE") CALL_TYPE: String,
        @Field("CALL_NUMBER") CALL_NUMBER: String
    ): Call<stateModel>
}


interface InsertMessageInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertMessageInfo)
    fun getInsertMessageInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("MESSAGE_ID") MESSAGE_ID: String,
        @Field("MESSAGE_TEXT") MESSAGE_TEXT: String,
        @Field("MESSAGE_DATE") MESSAGE_DATE: String,
        @Field("MESSAGE_NUMBER") MESSAGE_NUMBER: String,
    ): Call<stateModel>
}

interface InsertPictureInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertPictureInfo)
    fun getInsertPictureInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("PICTURE_ID") PICTURE_ID: String,
        @Field("PICTURE_TITLE") PICTURE_TITLE: String,
        @Field("LATITUDE") LATITUDE: String,
        @Field("LONGITUDE") LONGITUDE: String,
        @Field("PICTURE_DATE") PICTURE_DATE: String,
        @Field("PICTURE_TYPE") PICTURE_TYPE: String,
    ): Call<stateModel>
}

interface InsertStepCountInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertStepCountInfo)
    fun getInsertStepCountInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("STEP_COUNT") STEP_COUNT: Int,
        @Field("STEP_DATE") STEP_DATE: String,
        @Field("LAST_STEP_DATE") LAST_STEP_DATE: String,
    ): Call<stateModel>
}

interface InsertWalkingInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertWalkingInfo)
    fun getInsertWalkingInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("WALKING_VELOCITY") WALKING_VELOCITY: Double,
        @Field("START_TIME") START_TIME: String,
        @Field("END_TIME") END_TIME: String,
        @Field("WALKING_TIME") WALKING_TIME: Long,
        @Field("WALKING_DISTANCE") WALKING_DISTANCE: Double,
    ): Call<stateModel>
}

interface InsertSleepInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertSleepInfo)
    fun getInsertSleepInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("START_TIME") START_TIME: String,
        @Field("END_TIME") END_TIME: String,
        @Field("SLEEP_TIME") SLEEP_TIME: Long,
    ): Call<stateModel>
}

interface insertHeartRateInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertHeartRateInfo)
    fun getInsertHeartRateInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("HEART_RATE") HEART_RATE: Long,
        @Field("RATE_TIME") RATE_TIME: String,
        ): Call<stateModel>
}

interface insertLuxInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertLuxInfo)
    fun getInsertLuxInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("LUX_VALUE") LUX_VALUE: Long,
        @Field("LUX_TIME") LUX_TIME: String,
    ): Call<stateModel>
}

interface insertDbInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertDbInfo)
    fun getInsertDbInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("DB_VALUE") DB_VALUE: Int,
        @Field("DB_TIME") DB_TIME: String,
    ): Call<stateModel>
}