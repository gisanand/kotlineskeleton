package com.greatinnovus.promotionapp.app

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.greatinnovus.promotionapp.BasePackage.BaseActivity
import com.greatinnovus.skeleton.R
import com.greatinnovus.skeleton.databinding.ActivityAdminBinding

class AdminActivity : BaseActivity() {
lateinit var adminviewmodel:AdminViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_admin)

        adminviewmodel= AdminViewmodel(mActivity)
      val adminBinding: ActivityAdminBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_admin)
        adminBinding.adminmodle=adminviewmodel;
        adminBinding.executePendingBindings()
    }
}
