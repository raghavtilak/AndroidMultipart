package com.raghav.multipartexample

import android.content.Context
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;
import okhttp3.OkHttpClient;

@GlideModule
class MyGildeModule: AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val okHttpClient: OkHttpClient? = Util.getUnsafeOkHttpClient()
        okHttpClient?.let {
            registry.replace(GlideUrl::class.java,InputStream::class.java,OkHttpUrlLoader.Factory(it));
        }
    }

}