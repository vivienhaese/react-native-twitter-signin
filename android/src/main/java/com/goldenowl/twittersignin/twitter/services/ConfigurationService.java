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

package com.goldenowl.twittersignin.twitter.services;

import com.goldenowl.twittersignin.twitter.models.Configuration;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConfigurationService {
    /**
     * Returns the current configuration used by Twitter including twitter.com slugs which are not
     * user names, maximum photo resolutions, and t.co URL lengths.
     */
    @GET("/1.1/help/configuration.json")
    Call<Configuration> configuration();
}
