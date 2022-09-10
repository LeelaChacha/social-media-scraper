/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper.reddit.models;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class Post {
    @SerializedName(value = "id")
    @JsonAdapter(SmsGlobalIdAdapter.class)
    private String id;

    @SerializedName(value = "name")
    private String redditId;

    @SerializedName(value = "title")
    private String title;

    @SerializedName(value = "author")
    private String author;

    @SerializedName(value = "selftext")
    private String self;

    @SerializedName(value = "url")
    private String url;

    @SerializedName(value = "created_utc")
    @JsonAdapter(UnixTimestampAdapter.class)
    private Date createdAtUtc;

    @SerializedName(value = "score")
    private Integer score;

    @SerializedName(value = "subreddit")
    private String subredditName;

    @SerializedName(value = "subreddit_subscribers")
    private Integer subredditSubscribers;

    @SerializedName(value = "total_awards_received")
    private Integer awardsReceived;

    @SerializedName(value = "hidden")
    private boolean isHidden;

    @SerializedName(value = "is_original_content")
    private boolean isOriginalContent;

    @SerializedName(value = "is_meta")
    private boolean isMeta;

    @SerializedName(value = "over_18")
    private boolean isNsfw;
}
