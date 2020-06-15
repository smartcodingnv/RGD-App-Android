package com.rgddev.app.model


import com.google.gson.annotations.SerializedName

data class FirebaseData(
    @SerializedName("end_date")
    val endDate: String? = "",
    @SerializedName("image")
    val image: String? = "",
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("green_str")
    val greenStr: String? = "",
    @SerializedName("start_date")
    val startDate: String? = "",
    @SerializedName("category")
    val category: String? = "",
    @SerializedName("refrence_record_id")
    val refrenceRecordId: Int = 0
)


