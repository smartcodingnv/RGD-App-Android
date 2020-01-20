package com.rgddev.app.responce.news


import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("date")
                    val date: String = "",
                    @SerializedName("image")
                    val image: List<String>?,
                    @SerializedName("description")
                    val description: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("title")
                    val title: String = "",
                    @SerializedName("vides")
                    val vides: List<String>?)


data class ResponceNews(@SerializedName("data")
                        val data: List<DataItem>?,
                        @SerializedName("api_status")
                        val apiStatus: Int = 0,
                        @SerializedName("message")
                        val message: String = "")


