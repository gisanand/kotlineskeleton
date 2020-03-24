package com.greatinnovus.promotionapp.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.greatinnovus.promotionapp.constants.StringConstants
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class App : Application() {
init {
    mContext=this;
}

    companion object shared {
        lateinit var mContext:Context
        lateinit var sharedPreferences: SharedPreferences;
        lateinit var editor: SharedPreferences.Editor

        public fun getSharedPreferenceval(): SharedPreferences {
            return sharedPreferences
        }


    }


    override fun onCreate() {
        super.onCreate()

        sharedPreferences =
            applicationContext.getSharedPreferences(StringConstants.APP_PREFS_ACCOUNT, 0)
        editor = sharedPreferences.edit()
        editor.apply()

        ssl()
    }

    public fun ssl() {
        try {
            val victimizedManager = arrayOf<TrustManager>(

                object : X509TrustManager {

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                })

            val sc = SSLContext.getInstance("SSL");
            sc.init(null, victimizedManager, SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (e: Exception) {
            e.printStackTrace();
        }
    }


}

