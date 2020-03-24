package com.greatinnovus.promotionapp.volleyutils


import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader

class LruBitmapCache : LruCache<String, Bitmap>,ImageLoader.ImageCache
{
    constructor(maxSize: Int) :  super(maxSize)





    override fun sizeOf(key: String, value: Bitmap): Int {
        return value.rowBytes * value.height / 1024
    }

    override fun getBitmap(url: String): Bitmap {
        return get(url)
    }

    override fun putBitmap(url: String, bitmap: Bitmap) {
        put(url, bitmap)
    }

}