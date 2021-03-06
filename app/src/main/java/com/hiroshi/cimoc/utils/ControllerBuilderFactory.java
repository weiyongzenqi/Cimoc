package com.hiroshi.cimoc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilderSupplier;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.hiroshi.cimoc.CimocApplication;
import com.hiroshi.cimoc.core.Kami;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Hiroshi on 2016/7/8.
 */
public class ControllerBuilderFactory {

    private static SparseArray<PipelineDraweeControllerBuilder> builderArray = new SparseArray<>();

    public static PipelineDraweeControllerBuilder getControllerBuilder(int source, Context context) {
        if (builderArray.get(source) == null) {
            ImagePipelineFactory factory = buildFactory(context.getApplicationContext(), source);
            builderArray.put(source, new PipelineDraweeControllerBuilderSupplier(context.getApplicationContext(), factory).get());
        }
        return builderArray.get(source);
    }

    private static ImagePipelineFactory buildFactory(Context context, final int source) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String referer = Kami.getRefererById(source);
                Request.Builder request = chain.request().newBuilder();
                request.addHeader("Referer", referer);
                return chain.proceed(request.build());
            }
        }).build();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(context, client)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        return new ImagePipelineFactory(config);
    }

}
