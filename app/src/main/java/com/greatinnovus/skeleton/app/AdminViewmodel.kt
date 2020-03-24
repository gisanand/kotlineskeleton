package com.greatinnovus.promotionapp.app

import android.app.Activity
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR.currenturl
import com.greatinnovus.promotionapp.BasePackage.BaseviewModel
import com.greatinnovus.promotionapp.constants.StringConstants
import com.greatinnovus.promotionapp.constants.UrlConstants
import com.greatinnovus.promotionapp.utils.isHavingvalue
import com.greatinnovus.promotionapp.utils.loadData
import com.greatinnovus.promotionapp.utils.saveData
import androidx.databinding.library.baseAdapters.BR
class AdminViewmodel : BaseviewModel {
    @Bindable
    var currenturl: String = ""

    var updatedurl: String = ""
  var  mActivity: Activity?=null
    constructor(mActivity: Activity) {
        updatecurrenturl()
    }
val urlone="http://192.168.1.44:5000/api/"
    val urltwo=""
    private fun updatecurrenturl() {
        val oldurl: String = StringConstants.PREF_BASEURL.loadData(1) as String
        if (oldurl.isHavingvalue()) {
            setBaseurls(oldurl)
        }else{
            setBaseurls(UrlConstants.BaseURL)
        }
    }

    private fun setBaseurls(oldurl: String) {
        currenturl = oldurl
        notifyPropertyChanged(BR.currenturl)


    }

    public fun baseurlchanged(s: CharSequence, inputtype: Int) {
        updatedurl = s.toString()
    }

    public fun fetchurl(s: Int) {
        if(s==1) {
            setBaseurls(urlone)
        }
    }

    public fun saveAndRestart() {
        StringConstants.PREF_BASEURL.saveData(updatedurl)
        mActivity!!.finish()
    }


}

