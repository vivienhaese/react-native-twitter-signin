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

package com.goldenowl.twittersignin.twitter;

import com.goldenowl.twittersignin.twitter.internal.TwitterApi;
import com.goldenowl.twittersignin.twitter.internal.network.OkHttpClientHelper;
import com.goldenowl.twittersignin.twitter.models.BindingValues;
import com.goldenowl.twittersignin.twitter.models.BindingValuesAdapter;
import com.goldenowl.twittersignin.twitter.models.SafeListAdapter;
import com.goldenowl.twittersignin.twitter.models.SafeMapAdapter;
import com.goldenowl.twittersignin.twitter.services.AccountService;
import com.goldenowl.twittersignin.twitter.services.CollectionService;
import com.goldenowl.twittersignin.twitter.services.ConfigurationService;
import com.goldenowl.twittersignin.twitter.services.FavoriteService;
import com.goldenowl.twittersignin.twitter.services.ListService;
import com.goldenowl.twittersignin.twitter.services.MediaService;
import com.goldenowl.twittersignin.twitter.services.SearchService;
import com.goldenowl.twittersignin.twitter.services.StatusesService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A class to allow authenticated access to Twitter API endpoints.
 * Can be extended to provided additional endpoints by extending and providing Retrofit API
 * interfaces to {@link TwitterApiClient#getService(Class)}
 */
public class TwitterApiClient {
    final ConcurrentHashMap<Class, Object> services;
    final Retrofit retrofit;

    /**
     * Constructs Guest Session based TwitterApiClient.
     */
    public TwitterApiClient() {
        this(OkHttpClientHelper.getOkHttpClient(
                TwitterCore.getInstance().getGuestSessionProvider()), new TwitterApi());
    }

    /**
     * Constructs Guest Session based TwitterApiClient, with custom http client.
     *
     * The custom http client can be constructed with {@link okhttp3.Interceptor}, and other
     * optional params provided in {@link okhttp3.OkHttpClient}.
     */
    public TwitterApiClient(OkHttpClient client) {
        this(OkHttpClientHelper.getCustomOkHttpClient(
                client,
                TwitterCore.getInstance().getGuestSessionProvider()),
            new TwitterApi());
    }

    /**
     * Constructs User Session based TwitterApiClient.
     */
    public TwitterApiClient(TwitterSession session) {
        this(OkHttpClientHelper.getOkHttpClient(
                session,
                TwitterCore.getInstance().getAuthConfig()),
            new TwitterApi());
    }

    /**
     * Constructs User Session based TwitterApiClient, with custom http client.
     *
     * The custom http client can be constructed with {@link okhttp3.Interceptor}, and other
     * optional params provided in {@link okhttp3.OkHttpClient}.
     */
    public TwitterApiClient(TwitterSession session, OkHttpClient client) {
        this(OkHttpClientHelper.getCustomOkHttpClient(
                client,
                session,
                TwitterCore.getInstance().getAuthConfig()),
            new TwitterApi());
    }

    TwitterApiClient(OkHttpClient client, TwitterApi twitterApi) {
        this.services = buildConcurrentMap();
        this.retrofit = buildRetrofit(client, twitterApi);
    }

    private Retrofit buildRetrofit(OkHttpClient httpClient, TwitterApi twitterApi) {
        return new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(twitterApi.getBaseHostUrl())
                .addConverterFactory(GsonConverterFactory.create(buildGson()))
                .build();
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new SafeListAdapter())
                .registerTypeAdapterFactory(new SafeMapAdapter())
                .registerTypeAdapter(BindingValues.class, new BindingValuesAdapter())
                .create();
    }

    private ConcurrentHashMap buildConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * @return {@link AccountService} to access TwitterApi
     */
    public AccountService getAccountService() {
        return getService(AccountService.class);
    }

    /**
     * @return {@link FavoriteService} to access TwitterApi
     */
    public FavoriteService getFavoriteService() {
        return getService(FavoriteService.class);
    }

    /**
     * @return {@link StatusesService} to access TwitterApi
     */
    public StatusesService getStatusesService() {
        return getService(StatusesService.class);
    }

    /**
     * @return {@link SearchService} to access TwitterApi
     */
    public SearchService getSearchService() {
        return getService(SearchService.class);
    }

    /**
     * @return {@link ListService} to access TwitterApi
     */
    public ListService getListService() {
        return getService(ListService.class);
    }

    /**
     * Use CollectionTimeline directly, CollectionService is expected to change.
     * @return {@link CollectionService} to access TwitterApi
     */
    public CollectionService getCollectionService() {
        return getService(CollectionService.class);
    }

    /**
     * @return {@link ConfigurationService} to access TwitterApi
     */
    public ConfigurationService getConfigurationService() {
        return getService(ConfigurationService.class);
    }

    /**
     * @return {@link MediaService} to access Twitter API
     * upload endpoints.
     */
    public MediaService getMediaService() {
        return getService(MediaService.class);
    }

    /**
     * Converts Retrofit style interface into instance for API access
     *
     * @param cls Retrofit style interface
     * @return instance of cls
     */
    @SuppressWarnings("unchecked")
    protected <T> T getService(Class<T> cls) {
        if (!services.contains(cls)) {
            services.putIfAbsent(cls, retrofit.create(cls));
        }
        return (T) services.get(cls);
    }
}
