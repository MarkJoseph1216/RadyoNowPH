package com.radyopilipinomediagroup.radyo_now.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StationListResponse {
    @SerializedName("kind")
    var kind: String? = null

    @SerializedName("etag")
    var etag: String? = null

    @SerializedName("nextPageToken")
    var nextPageToken: String? = null

    @SerializedName("regionCode")
    var regionCode: String? = null

    @SerializedName("items")
    var items: List<Item>? = null

    inner class Item {
        @SerializedName("kind")
        @Expose
        var kind: String? = null

        @SerializedName("etag")
        @Expose
        var etag: String? = null

        @SerializedName("id")
        @Expose
        var id: Id? = null

        @SerializedName("snippet")
        @Expose
        var snippet: Snippet? = null

        inner class Id {
            @SerializedName("kind")
            @Expose
            var kind: String? = null

            @SerializedName("videoId")
            @Expose
            var videoId: String? = null
        }

        inner class Snippet {
            @SerializedName("publishedAt")
            @Expose
            var publishedAt: String? = null

            @SerializedName("channelId")
            @Expose
            var channelId: String? = null

            @SerializedName("title")
            @Expose
            var title: String? = null

            @SerializedName("description")
            @Expose
            var description: String? = null

            @SerializedName("thumbnails")
            @Expose
            var thumbnails: Thumbnails? = null

            @SerializedName("channelTitle")
            @Expose
            var channelTitle: String? = null

            @SerializedName("liveBroadcastContent")
            @Expose
            var liveBroadcastContent: String? = null

            @SerializedName("publishTime")
            @Expose
            var publishTime: String? = null

            inner class Thumbnails {
                @SerializedName("default")
                @Expose
                var default: Default? = null

                @SerializedName("medium")
                @Expose
                var medium: Default? = null

                @SerializedName("high")
                @Expose
                var high: Default? = null

                inner class Default {
                    @SerializedName("url")
                    @Expose
                    var url: String? = null

                    @SerializedName("width")
                    @Expose
                    var width: Int? = null

                    @SerializedName("height")
                    @Expose
                    var height: Int? = null
                }
            }
        }
    }
}