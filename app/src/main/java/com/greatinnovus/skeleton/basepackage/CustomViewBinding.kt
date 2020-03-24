package com.greatinnovus.promotionapp.BasePackage

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.greatinnovus.promotionapp.utils.CommonUtils
import com.greatinnovus.skeleton.R


class CustomViewBinding {








}

@BindingAdapter("loadImage")
public fun LoadImage(view: ImageView, imageUrl:String)
{

 val corneer=   CommonUtils.convertDpToPixel(12f, view.context).toInt()
CommonUtils.log("TestURL","Image url   $imageUrl  cornner $corneer    Image view width ${view.width} height ${view.height}")
    val glideUrl = GlideUrl(
        imageUrl,
        LazyHeaders.Builder()
            .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InN1cGVyYWRtaW5AZ21haWwuY29tIiwiaWF0IjoxNTc3OTQyNTE4fQ.PIJDeVGFR62Etz-8RRuf3L8ZGoSb7kHwH0CuE9M6lzA")
            .build()
    )
    Glide.with(view.getContext())
        .load(glideUrl)
        .centerCrop()
        .placeholder(CommonUtils.getcircleprogressbar(view.getContext(), R.color.applogocolor))
        .error(R.drawable.ic_launcher_background)
        .apply(
            RequestOptions()
                .placeholder(CommonUtils.getcircleprogressbar(view.getContext(), R.color.applogocolor))
                .override(view.width,view.height)
                .transform( RoundedCorners(corneer))
                .placeholder(CommonUtils.getcircleprogressbar(view.getContext(), R.color.applogocolor))
                .error(R.drawable.ic_launcher_background)
        )

         //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.skipMemoryCache(true)


        .into(view)





}

@BindingAdapter("setAdapter")
fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>? )
{
    recyclerView.setHasFixedSize(true)
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter
}