package com.rgddev.app.responce.insuranceplan


import com.google.gson.annotations.SerializedName

data class ResponceInsurancePlan(
    @SerializedName("data")
    val data: List<DataItem>?,
    @SerializedName("api_status")
    val apiStatus: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


data class DataItem(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: Int = 0
)


