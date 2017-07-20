package com.example.neel.bookingapp.Other.DB;

import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Map;

/**
 * Created by sushrutshringarputale on 5/27/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class ExpressRestClient {
    private static final String BASE_URL = "https://bookmyturf.ap-south-1.elasticbeanstalk.com";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, @Nullable Map<String, String> headers, AsyncHttpResponseHandler responseHandler) {
        if (headers != null) {
            for (String key : headers.keySet()) {
                client.addHeader(key, headers.get("key"));
            }
        }
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, @Nullable Map<String, String> headers, AsyncHttpResponseHandler responseHandler) {
        if (headers != null) {
            for (String key : headers.keySet()) {
                client.addHeader(key, headers.get("key"));
            }
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("ABSOLUTE URL", BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

}
