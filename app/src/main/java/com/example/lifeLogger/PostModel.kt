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