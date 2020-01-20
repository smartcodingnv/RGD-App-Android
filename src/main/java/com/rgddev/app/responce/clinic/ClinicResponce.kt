package com.rgddev.app.responce.clinic


import com.google.gson.annotations.SerializedName

data class ClinicResponce(@SerializedName("data")
                          val data: List<DataItem>?,
                          @SerializedName("api_status")
                          val apiStatus: Int = 0,
                          @SerializedName("message")
                          val message: String = "")


data class DataItem(@SerializedName("address")
                    val address: String = "",
                    @SerializedName("phone")
                    val phone: List<PhoneItem>?,
                    @SerializedName("latitude")
                    val latitude: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("services")
                    val services: List<ServicesItem>?,
                    @SerializedName("longitude")
                    val longitude: String = "")


data class PhoneItem(@SerializedName("ext")
                     val ext: String = "",
                     @SerializedName("phone")
                     val phone: String = "")


data class ServicesItem(@SerializedName("working_times")
                        val workingTimes: List<WorkingTimesItem>?,
                        @SerializedName("name")
                        val name: String = "",
                        @SerializedName("service_detail")
                        val serviceDetail: String = "",
                        @SerializedName("clinic_has_service_id")
                        val clinicHasServiceId: Int = 0,
                        @SerializedName("providers")
                        val providers: List<String>?)

{
    private var expanded: Boolean = false

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }

    fun isExpanded(): Boolean {
        return expanded
    }
}

data class WorkingTimesItem(@SerializedName("working_days")
                            val workingDays: String = "",
                            @SerializedName("working_time")
                            val workingHours: String = "")


