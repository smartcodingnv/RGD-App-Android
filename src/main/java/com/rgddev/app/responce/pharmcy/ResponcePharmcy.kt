package com.rgddev.app.responce.pharmcy


import com.google.gson.annotations.SerializedName

data class ResponcePharmcy(@SerializedName("data")
                           val data: List<DataItem>?,
                           @SerializedName("api_status")
                           val apiStatus: Int = 0,
                           @SerializedName("message")
                           val message: String = "")


data class DataItem(@SerializedName("location_name")
                    val locationName: String = "",
                    @SerializedName("address")
                    val address: String = "",
                    @SerializedName("phone")
                    val phone: List<PhoneItem>?,
                    @SerializedName("working_times")
                    val workingTimes: List<WorkingTimesItem>?,
                    @SerializedName("latitude")
                    val latitude: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("service_detail")
                    val serviceDetail: String = "",
                    @SerializedName("clinic_has_service_id")
                    val clinicHasServiceId: Int = 0,
                    @SerializedName("longitude")
                    val longitude: String = "")


data class PhoneItem(@SerializedName("ext")
                     val ext: String = "",
                     @SerializedName("phone")
                     val phone: String = "")


data class WorkingTimesItem(@SerializedName("working_time")
                            val workingTime: String = "",
                            @SerializedName("working_days")
                            val workingDays: String = "")


