package com.example.kotlinsample.volleyutils

import android.content.Context
import android.graphics.Bitmap
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.greatinnovus.promotionapp.volleyutils.LruBitmapCache

class VolleySingleton {


    public lateinit var mCtx: Context
    private var mRequestQueue: RequestQueue? = null
    private lateinit var mImageLoader: ImageLoader

    constructor(mCtx: Context) {
        this.mCtx = mCtx
        mRequestQueue = getRequestQueue()

        mImageLoader = ImageLoader(mRequestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruBitmapCache(getDefaultLruCacheSize())

                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })

    }


    companion object {
        private var mInstance: VolleySingleton? = null
    @Synchronized
    fun getInstance(context: Context): VolleySingleton {

        if (mInstance == null) {
            mInstance = VolleySingleton(context)
        }
        return mInstance!!
    }
}

    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.applicationContext)
        }
        return mRequestQueue!!
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        getRequestQueue().add(req)
    }


    fun getImageLoader(): ImageLoader {
        return mImageLoader
    }

    fun getDefaultLruCacheSize(): Int {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        return maxMemory / 8
    }



}