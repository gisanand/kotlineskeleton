package com.greatinnovus.promotionapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.StrictMode.VmPolicy
import android.os.StrictMode.setVmPolicy
import android.provider.MediaStore
import com.greatinnovus.promotionapp.constants.StringConstants
import com.greatinnovus.skeleton.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class Imagecaptureutils : EasyPermissions.PermissionCallbacks {

    val TAG = "Imagecaptureutils"


    lateinit var mActivity: Activity;
    lateinit var imageUri: Uri
    lateinit var imageBitmap: Bitmap

    var typerequestcode: Int = 0

    var needGalleryAcess: Boolean = false
    lateinit var imagecaptureutils: Imagecaptureutils

    lateinit var listiner: DataListiner

    constructor(mActivity: Activity) {
        this.mActivity = mActivity
    }

    constructor(mActivity: Activity, typerequestcode: Int) {
        this.mActivity = mActivity
        this.typerequestcode = typerequestcode
        checkpremission()
    }


    constructor(mActivity: Activity, typerequestcode: Int, needGalleryAcess: Boolean) {
        this.mActivity = mActivity
        this.typerequestcode = typerequestcode
        this.needGalleryAcess = needGalleryAcess
        checkpremission()
    }


    public fun checkpremission() {
        if (typerequestcode == StringConstants.REQ_LOCATION_RESULT || typerequestcode == StringConstants.REQ_LOCATION_RESULT_ONLY) {

            if (!EasyPermissions.hasPermissions(
                    mActivity, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {

                EasyPermissions.requestPermissions(
                    mActivity,
                    mActivity.resources.getString(R.string.permissioncontent),
                    StringConstants.RC_CAMERA_AND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            } else {
                calllocationpickscreen();
            }
        } else {
            if (!EasyPermissions.hasPermissions(
                    mActivity, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                EasyPermissions.requestPermissions(
                    mActivity,
                    mActivity.resources.getString(R.string.permissioncontent),
                    StringConstants.RC_CAMERA_AND_CAMERA,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                );

            } else {
                callimagepickscreen();
            }
        }

    }

    @AfterPermissionGranted(StringConstants.RC_CAMERA_AND_LOCATION)
    fun calllocationpickscreen() {
        pickLocation()
    }


    @AfterPermissionGranted(StringConstants.RC_CAMERA_AND_CAMERA)
    fun callimagepickscreen() {
        CommonUtils.log(TAG, "$needGalleryAcess Image needGalleryAcess ");
        if (needGalleryAcess) {
            // showCameraDialog(mActivity);
        } else {
            callCamera(mActivity);
        }

    }


    fun pickLocation() {
        /*  val intent =  Intent(mActivity, Shoplocationscreen.class);
          mActivity.startActivityForResult(intent, StringConstants.REQ_LOCATION_RESULT);*/
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
    }


    interface DataListiner {
        public fun onDataSucess(requestCode: Int, data: Intent);
    }

    public fun callCamera(context: Context) {

        val builder = VmPolicy.Builder();
        setVmPolicy(builder.build());
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        val output = File(dir, "Agrouserimage.jpeg");




        imageUri =CommonUtils. getcurrentimageuri();
        CommonUtils.log(TAG, " Image uri before " + imageUri.getPath());
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        (context as Activity).startActivityForResult(intent, StringConstants.REQUEST_CAMERA);


    }
}