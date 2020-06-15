package com.rgddev.app.responce.contactlist


import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("address")
    val address: String = "",
    @SerializedName("phone")
    val phone: List<PhoneItem>?,
    @SerializedName("opening_time")
    val openingTime: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("email")
    val email: String = ""
)


data class ResponceContactList(
    @SerializedName("data")
    val data: List<DataItem>?,
    @SerializedName("api_status")
    val apiStatus: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


data class PhoneItem(
    @SerializedName("ext")
    val ext: String = "",
    @SerializedName("phone")
    val phone: String = ""
)


