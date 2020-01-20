package com.rgddev.app.responce.insurancedetails


import com.google.gson.annotations.SerializedName

data class ResponceMedicnList(@SerializedName("data")
                              val data: List<DataItem>?,
                              @SerializedName("api_status")
                              val apiStatus: Int = 0,
                              @SerializedName("message")
                              val message: String = "")


data class DataItem(@SerializedName("name")
                    val name: String = "")


