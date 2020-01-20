package com.rgddev.app.responce.notificationlist


import com.google.gson.annotations.SerializedName

data class ResponceNotification(
    @SerializedName("data")
    val data: List<DataItem>?,
    @SerializedName("api_status")
    val apiStatus: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


data class GuardTimetable(
    @SerializedName("end_date_time")
    val endDateTime: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("start_date_time")
    val startDateTime: String = "",
    @SerializedName("image")
    val image: List<String>?
)


data class DataItem(
    @SerializedName("news")
    val news: News,
    @SerializedName("type_id")
    val typeId: Int = 0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("type")
    val type: String = "",
    @SerializedName("guard_timetable")
    val guardTimetable: GuardTimetable,
    @SerializedName("title")
    val title: String = ""
)


data class News(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("image")
    val image: List<String>?,
    @SerializedName("vides")
    val vides: List<String>?
)


