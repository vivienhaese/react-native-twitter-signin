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

package com.goldenowl.twittersignin.twitter.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents Media which has been uploaded to Twitter upload endpoints.
 */
public class Media {

    @SerializedName("media_id")
    public final long mediaId;

    @SerializedName("media_id_string")
    public final String mediaIdString;

    @SerializedName("size")
    public final long size;

    @SerializedName("image")
    public final Image image;

    public Media(long mediaID, String mediaIdString, long size, Image image) {
        this.mediaId = mediaID;
        this.mediaIdString = mediaIdString;
        this.size = size;
        this.image = image;
    }
}
