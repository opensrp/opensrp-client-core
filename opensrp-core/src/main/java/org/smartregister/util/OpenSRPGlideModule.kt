package org.smartregister.util

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * Apps using Glide are required to extend the the AppGlideModule class from the Glide library.
 * Any options can be overridden in the ApplyOptions override method as show below.
 * For more on the configuration see https://guides.codepath.com/android/Displaying-Images-with-the-Glide-Library
 * */
@GlideModule
class OpenSRPGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setLogLevel(Log.ERROR);
    }
}