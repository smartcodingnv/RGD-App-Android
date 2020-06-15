package com.rgddev.app.utils

class AppConfig {

    object URL {

        val BASE_URL = "http://rgdapp.com/api/"
        //val BASE_URL = "http://rgd.myteamspace.org/api/"
        //val BASE_URL = "http://rgddev.myteamspace.org/api/"

        const val TOKEN = "HRKUXIl4ouqWa88Kefr17LQiG5YZemShNqx0WMkkPtNSHLR8cAPXwStz1KLhQy37"
        const val URL_NEWS = "news"
        const val URL_NEWSDETAILS = "news/detail"
        const val URL_NEWSINSURANCE = "insurance_plan"
        const val URL_NEWSINSURANCEDETAILS = "insurance_plan/detail"
        const val URL_GAURDTIMETABLE = "gaurd_time_table"
        const val URL_GAURDDETAILS = "gaurd_time_table/detail"
        const val URL_CONTACTLIST = "contact_list"
        const val URL_PROVIDER = "providers"
        const val URL_PHARMACY = "pharmacy"
        const val URL_LAB = "lab"
        const val URL_CLINIC = "clinic"
        const val URL_NOTIFICATION = "Notification"


        val SUCCESS = 200
        val TOKEN_EXPIRE = 400

    }

    object PREFERENCE {

        val PREF_FILE = "rgdaoo"
        val USER_LOGIN_CHECK = "login"
        val PROVIDERRESPONCE = "providerresponce"
        val PHARMACYRESPONCE = "pharmacyresponce"
        val LABRESPONCE = "labresponce"
        val CLINICRESPONCE = "clinicresponce"
        val INSURANCEPLAN = "insuranceplan"
        val MEDICINLIST = "medicinlist"
        val CONTACTUS = "contactus"
        val GARDRESPONCE = "gardresponce"
        val NEWSRESPONCE = "newsresponce"
        val NOTIFICATIONRESPONCE = "notificationresponce"


    }

    object BUNDLE {

        val CHECKDISCRIPTION = "checkdiscription" // 1-place , = 2- tour, 3-contact us
        val CHECKANIMATIES =
            "checkanimaties" // 1-place , = 2- tour, 3-contact us , 4 - bookinfo touramintes, 5- bookinfo events amintes


    }

    object EXTRA {
        val ACTION_SEARCH = "action_search"
        val SEARCHKEY = "searchkey"
        val SEARCHVALUE = "jjj"
        val FCM_GROUP = "rydemate"
        val NEWSID = "newsid"
        val NEWSCHECK = "newscheck"
        val INSUSRANCEID = "insid"
        val INSUSRANCETITLE = "instile"
        val GUARDID = "guardid"

        val CLINICID = "clinincid"
        val CLNICNAME = "clinincname"
        val CLNICADDRESS = "clinincaddress"
        val CLNICPHONENO = "clinincphoneno"
        val CLNICSERVICE = "clinincservice"

        val ACTION_NOTIFICAION = "action_notificaion"
        val CHECKNOTIFICAION = "checknotificaion"

        const val FROM_PHARMACY_OR_PRACTICE = "from_pharmacy_or_practice"


    }

    object Constant {
        val LANGUAGE = "en"
        val LISTLIMIT = "10"

        const val TYPE_GUARD_TIME_TABLE = "GUARD_TIMETABLE"

    }
}