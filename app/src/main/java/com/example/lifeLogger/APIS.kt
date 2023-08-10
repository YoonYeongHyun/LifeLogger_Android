package com.example.lifeLogger

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


interface insertAppsInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertAppsInfo)
    fun getInsertAppsInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("ACC_COUNT") ACC_COUNT: Int,
        @Field("AUDIO_COUNT") AUDIO_COUNT: Int,
        @Field("GAME_COUNT") GAME_COUNT: Int,
        @Field("IMAGE_COUNT") IMAGE_COUNT: Int,
        @Field("MAPS_COUNT") MAPS_COUNT: Int,
        @Field("NEWS_COUNT") NEWS_COUNT: Int,
        @Field("SOCIAL_COUNT") SOCIAL_COUNT: Int,
        @Field("VIDEO_COUNT") VIDEO_COUNT: Int,
        @Field("PRODUCT_COUNT") PRODUCT_COUNT: Int,
    ): Call<stateModel>
}

interface deleteAppsInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.deleteAppsInfo)
    fun getDeleteAppsInfo(
        @Field("USER_ID") USER_ID: String?,
    ): Call<stateModel>
}


interface SelectTodayStateAPI {
    @FormUrlEncoded
    @POST(MyApi.selectTodayState)
    fun getSelectTodayState(
        @Field("USER_ID") USER_ID: String?,
        @Field("STATE_DATE") STATE_DATE: String?,
    ): Call<stateModel>
}


interface InsertTodayStateInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertTodayStateInfo)
    fun getInsertTodayStateInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("STATE_SCORE") STATE_SCORE: Int,
        @Field("STATE_DATE") STATE_DATE: String?,
    ): Call<stateModel>
}

interface insertUserInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertUserInfo)
    fun getInsertUserInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("USER_NAME") USER_NAME: String?,
        @Field("USER_PWD") USER_PWD: String?,
        @Field("USER_AGE") USER_AGE: String?,
        @Field("USER_GENDER") USER_GENDER: String?,
        @Field("USER_JOB") USER_JOB: String?,
        @Field("USER_FAMILY_CNT") USER_FAMILY_CNT: String?,
    ): Call<stateModel>
}

interface SelectSurveyListAPI {
    @FormUrlEncoded
    @POST(MyApi.selectSurveyList)
    fun getSelectSurveyList(
        @Field("USER_ID") USER_ID: String?,
    ): Call<surveyModel>
}

interface InsertSurveyInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.insertSurveyInfo)
    fun getInsertSurveyInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("SURVEY_SEQUENCE") SURVEY_SEQUENCE: String?,
        @Field("QUESTION_RESULT_1") QUESTION_RESULT_1: String?,
        @Field("QUESTION_RESULT_2") QUESTION_RESULT_2: String?,
        @Field("QUESTION_RESULT_3") QUESTION_RESULT_3: String?,
        @Field("QUESTION_RESULT_4") QUESTION_RESULT_4: String?,
        @Field("QUESTION_RESULT_5") QUESTION_RESULT_5: String?,
        @Field("QUESTION_RESULT_6") QUESTION_RESULT_6: String?,
        @Field("QUESTION_RESULT_7") QUESTION_RESULT_7: String?,
    ): Call<stateModel>
}

interface SelectDataSituationAPI {
    @FormUrlEncoded
    @POST(MyApi.selectDataSituation)
    fun getSelectDataSituation(
        @Field("USER_ID") USER_ID: String?,
        @Field("DATE_TIME") SURVEY_SEQUENCE: String?,
    ): Call<dataSituationModel>
}

interface SelectMyInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.selectMyInfo)
    fun getSelectMyInfo(
        @Field("USER_ID") USER_ID: String?
    ): Call<myInfoModel>
}

interface UpdateUserInfoAPI {
    @FormUrlEncoded
    @POST(MyApi.updateUserInfo)
    fun getUpdateUserInfo(
        @Field("USER_ID") USER_ID: String?,
        @Field("USER_AGE") USER_AGE: String?,
        @Field("USER_GENDER") USER_GENDER: String?,
        @Field("USER_JOB") USER_JOB: String?,
        @Field("USER_FAMILY_CNT") USER_FAMILY_CNT: String?,
    ): Call<stateModel>
}
