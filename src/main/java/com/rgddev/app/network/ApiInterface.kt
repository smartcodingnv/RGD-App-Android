package com.rgddev.app.network

import com.rgddev.app.responce.clinic.ClinicResponce
import com.rgddev.app.responce.contactlist.ResponceContactList
import com.rgddev.app.responce.garddetails.ResponceGardDetails
import com.rgddev.app.responce.gardtimetable.ResponceGardTimeTable
import com.rgddev.app.responce.insurancedetails.ResponceMedicnList
import com.rgddev.app.responce.insuranceplan.ResponceInsurancePlan
import com.rgddev.app.responce.lab.ResponceLab
import com.rgddev.app.responce.news.ResponceNews
import com.rgddev.app.responce.newsdetails.ResponceNewsDetails
import com.rgddev.app.responce.notificationlist.ResponceNotification
import com.rgddev.app.responce.pharmcy.ResponcePharmcy
import com.rgddev.app.responce.provider.ResponceProvider
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.AppConfig.URL.URL_CLINIC
import com.rgddev.app.utils.AppConfig.URL.URL_CONTACTLIST
import com.rgddev.app.utils.AppConfig.URL.URL_GAURDDETAILS
import com.rgddev.app.utils.AppConfig.URL.URL_GAURDTIMETABLE
import com.rgddev.app.utils.AppConfig.URL.URL_LAB
import com.rgddev.app.utils.AppConfig.URL.URL_NEWS
import com.rgddev.app.utils.AppConfig.URL.URL_NEWSDETAILS
import com.rgddev.app.utils.AppConfig.URL.URL_NEWSINSURANCE
import com.rgddev.app.utils.AppConfig.URL.URL_NEWSINSURANCEDETAILS
import com.rgddev.app.utils.AppConfig.URL.URL_NOTIFICATION
import com.rgddev.app.utils.AppConfig.URL.URL_PHARMACY
import com.rgddev.app.utils.AppConfig.URL.URL_PROVIDER
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiInterface {
    //
    //    @FormUrlEncoded
    //    @POST(AppConfig.URL.URL_REGISTER)
    //    Call<ResponceRegister> RESPONCE_REGISTER_CALL(@Field("email") String email, @Field("password") String password,
    //                                                  @Field("termcon") String term, @Field("accept") String condition,
    //                                                  @Field("firstname") String fname, @Field("lastname") String lname,
    //                                                  @Field("country") String contrycode, @Field("gender") String gender,
    //                                                  @Field("profile_pic") String profilepic, @Field("customerDOB") String coutdb);
    //

    /*  @FormUrlEncoded
      @POST(URL_LOGIN)
      fun LOGIN_CALL(
          @Field("email") email: String, @Field("password") passsord: String,
          @Field("lang_code") langcode: String
      ): Call<LoginResponce>*/

    @FormUrlEncoded
    @POST(URL_NEWS)
    fun CALL_NEWS(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceNews>

    @FormUrlEncoded
    @POST(URL_NEWSDETAILS)
    fun CALL_NEWSDETAILS(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String, @Field("id") id: String
    ): Call<ResponceNewsDetails>

    @FormUrlEncoded
    @POST(URL_NEWSINSURANCE)
    fun CALL_INSURANCEPLAN(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceInsurancePlan>

    @FormUrlEncoded
    @POST(URL_NEWSINSURANCEDETAILS)
    fun CALL_INSURANCEPLANDETAILS(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String,  @Field("id") id: String
    ): Call<ResponceMedicnList>

    @FormUrlEncoded
    @POST(URL_GAURDTIMETABLE)
    fun CALL_GAURDLIST(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceGardTimeTable>

    @FormUrlEncoded
    @POST(URL_GAURDDETAILS)
    fun CALL_GAURDDETAILS(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String,  @Field("id") id: String
    ): Call<ResponceGardDetails>

    @FormUrlEncoded
    @POST(URL_CONTACTLIST)
    fun CALL_CONTACTLIST(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceContactList>

    @FormUrlEncoded
    @POST(URL_PROVIDER)
    fun CALL_PROVIDERLIST(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceProvider>

    @FormUrlEncoded
    @POST(URL_PHARMACY)
    fun CALL_PHARMACYLIST(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponcePharmcy>

    @FormUrlEncoded
    @POST(URL_LAB)
    fun CALL_LABLIST(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceLab>

    @FormUrlEncoded
    @POST(URL_CLINIC)
    fun CALL_CLINICLIST(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ClinicResponce>


    @FormUrlEncoded
    @POST(URL_NOTIFICATION)
    fun CALL_NOTIFICATION(
        @Header("X-Authorization") token: String, @Field("sync_date") syncDate: String
    ): Call<ResponceNotification>


}
