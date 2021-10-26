package com.example.chat.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou


fun ImageView.load(uri: String) {
    if(uri.endsWith(".svg")) {
        GlideToVectorYou
            .init()
            .with(context)
            .requestBuilder
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .centerCrop())
            .into(this)
    } else {
        Glide.with(context).load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .centerCrop())
            .into(this)
    }
}