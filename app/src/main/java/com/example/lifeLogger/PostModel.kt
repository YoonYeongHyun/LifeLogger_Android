package com.example.lifeLogger

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.format.DateTimeFormatter

data class userModel(

    @Expose
    @SerializedName("USER_ID")
    var USER_ID: String?,

    @Expose
    @SerializedName("USER_PWD")
    var USER_PWD: String?,

    @Expose
    @SerializedName("USER_NAME")
    var USER_NAME: String?,

    @Expose
    @SerializedName("STATE")
    var STATE: String?,

    @Expose
    @SerializedName("MESSAGE")
    var MESSAGE: String?,
)


data class stateModel(

    @Expose
    @SerializedName("STATE")
    var STATE: String?,

    @Expose
    @SerializedName("MESSAGE")
    var MESSAGE: String?,
)


data class codeModel(

    @Expose
    @SerializedName("CODE_DT")
    var CODE_DT: String?,

    @Expose
    @SerializedName("STATE")
    var STATE: String?,

    @Expose
    @SerializedName("MESSAGE")
    var MESSAGE: String?,
)


data class surveyModel(

    @Expose
    @SerializedName("STATE")
    var STATE: String?,

    @Expose
    @SerializedName("MESSAGE")
    var MESSAGE: String?,

    @Expose
    @SerializedName("USER_ID")
    var USER_ID: String?,

    @Expose
    @SerializedName("JOIN_TIME")
    var JOIN_TIME: String?,

    @Expose
    @SerializedName("SECOND_SURVEY_DATE")
    var SECOND_SURVEY_DATE: String?,

    @Expose
    @SerializedName("THIRD_SURVEY_DATE")
    var THIRD_SURVEY_DATE: String?,

    @Expose
    @SerializedName("FIRST_SURVEY_FLAG")
    var FIRST_SURVEY_FLAG: String?,

    @Expose
    @SerializedName("SECOND_SURVEY_FLAG")
    var SECOND_SURVEY_FLAG: String?,

    @Expose
    @SerializedName("THIRD_SURVEY_FLAG")
    var THIRD_SURVEY_FLAG: String?,
)


data class dataSituationModel(

    @Expose
    @SerializedName("STATE")
    var STATE: String?,

    @Expose
    @SerializedName("MESSAGE")
    var MESSAGE: String?,

    @Expose
    @SerializedName("SLEEP_COUNT")
    var SLEEP_COUNT: String?,

    @Expose
    @SerializedName("HEART_COUNT")
    var HEART_COUNT: String?,

    @Expose
    @SerializedName("LUX_COUNT")
    var LUX_COUNT: String?,

    @Expose
    @SerializedName("DB_COUNT")
    var DB_COUNT: String?,

)

data class myInfoModel(

    @Expose
    @SerializedName("STATE")
    var STATE: String?,

    @Expose
    @SerializedName("MESSAGE")
    var MESSAGE: String?,

    @Expose
    @SerializedName("USER_AGE")
    var USER_AGE: String?,

    @Expose
    @SerializedName("USER_GENDER")
    var USER_GENDER: String?,

    @Expose
    @SerializedName("USER_JOB")
    var USER_JOB: String?,

    @Expose
    @SerializedName("USER_FAMILY_CNT")
    var USER_FAMILY_CNT: String?,


    )