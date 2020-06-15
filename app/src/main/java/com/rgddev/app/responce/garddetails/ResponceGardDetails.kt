package com.rgddev.app.responce.garddetails


import com.google.gson.annotations.SerializedName

data class ResponceGardDetails(
    @SerializedName("data")
    val data: Data,
    @SerializedName("api_status")
    val apiStatus: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


data class Data(
    @SerializedName("image")
    val image: List<String>?,
    @SerializedName("end_date_time")
    val endDateTime: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("start_date_time")
    val startDateTime: String = ""
)


