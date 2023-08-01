package com.example.lifeLogger

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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