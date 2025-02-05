/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.goldenowl.twittersignin.twitter.internal.oauth;

import com.goldenowl.twittersignin.twitter.TwitterCore;
import com.goldenowl.twittersignin.twitter.internal.TwitterApi;
import com.goldenowl.twittersignin.twitter.internal.network.OkHttpClientHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base class for OAuth service.
 */
abstract class OAuthService {

    private static final String CLIENT_NAME = "TwitterAndroidSDK";

    private final TwitterCore twitterCore;
    private final TwitterApi api;
    private final String userAgent;
    private final Retrofit retrofit;

    OAuthService(TwitterCore twitterCore, TwitterApi api) {
        this.twitterCore = twitterCore;
        this.api = api;
        userAgent = TwitterApi.buildUserAgent(CLIENT_NAME, twitterCore.getVersion());

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request request = chain.request().newBuilder()
                                .header("User-Agent", OAuthService.this.getUserAgent())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .certificatePinner(OkHttpClientHelper.getCertificatePinner())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(getApi().getBaseHostUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    protected TwitterCore getTwitterCore() {
        return twitterCore;
    }

    protected TwitterApi getApi() {
        return api;
    }

    protected String getUserAgent() {
        return userAgent;
    }

    protected Retrofit getRetrofit() {
        return retrofit;
    }
}
