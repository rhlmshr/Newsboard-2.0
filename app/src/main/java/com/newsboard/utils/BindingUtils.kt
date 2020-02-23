package com.newsboard.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.newsboard.R

object BindingUtils {

    @JvmStatic
    @BindingAdapter(
        value = ["app:imgUrl", "app:cornerRadius", "app:isCircular"],
        requireAll = false
    )
    fun loadImageAsync(
        view: ImageView,
        imgUrl: String?,
        cornerRadius: Int,
        isCircular: Boolean
    ) {
        var requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_img_placeholder)

        if (cornerRadius != 0) requestOptions =
            requestOptions.transform(CenterCrop(), RoundedCorners(cornerRadius))
        if (isCircular) requestOptions = requestOptions.transform(CircleCrop())

        Glide.with(view.context)
            .load(imgUrl)
            .apply(requestOptions)
            .into(view)
    }
}
