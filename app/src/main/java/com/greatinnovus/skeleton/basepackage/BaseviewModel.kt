package com.greatinnovus.promotionapp.BasePackage

import androidx.databinding.BaseObservable
import com.greatinnovus.promotionapp.constants.StringConstants
import com.greatinnovus.promotionapp.constants.UrlConstants
import com.greatinnovus.promotionapp.utils.isHavingvalue
import com.greatinnovus.promotionapp.utils.loadData
import com.greatinnovus.promotionapp.volleyutils.VolleyUtils

open class BaseviewModel : BaseObservable() {
    val volleyutils= VolleyUtils();
    var Baseurlold:String=""
    val Baseurl by lazy {
        val currenturl=StringConstants.PREF_BASEURL.loadData(1)as String
        if(currenturl.isHavingvalue())
        {
            currenturl
        }else{
            UrlConstants.BaseURL
        }
    }
init {

}


}