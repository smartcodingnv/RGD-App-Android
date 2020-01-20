package com.rgddev.app.responce.newsdetails


import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("date")
                val date: String = "",
                @SerializedName("image")
                val image: List<String>?,
                @SerializedName("vides")
                val video: List<String>?,
                @SerializedName("description")
                val description: String = "",
                @SerializedName("id")
                val id: Int = 0,
                @SerializedName("title")
                val title: String = "")


data class ResponceNewsDetails(@SerializedName("data")
                               val data: Data,
                               @SerializedName("api_status")
                               val apiStatus: Int = 0,
                               @SerializedName("message")
                               val message: String = "")


