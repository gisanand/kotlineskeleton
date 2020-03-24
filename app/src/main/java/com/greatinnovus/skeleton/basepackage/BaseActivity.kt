package com.greatinnovus.promotionapp.BasePackage

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.greatinnovus.promotionapp.volleyutils.VolleyUtils

open class BaseActivity : AppCompatActivity() {
val mActivity =this;
val volleyutils by lazy { VolleyUtils() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}