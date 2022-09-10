/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper.reddit;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import party.hukumraj_singh_deora.social_media_scraper.http.RateLimitedHttpClient;
import party.hukumraj_singh_deora.social_media_scraper.reddit.models.Post;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class RedditSdk {
    private final RateLimitedHttpClient rateLimitedHttpClient;
    private final double allowedRatePerSeconds = 59.0;
    private final String userAgent = "java:party.hukumraj_singh_deora_social_media_scraper:v1.0.0 (by u/krusader67)";
    private final String baseUrl = "https://oauth.reddit.com";
    private final String accessTokenUrl = "https://www.reddit.com/api/v1/access_token";
    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;
    private String transientAccessToken;

    public RedditSdk(String username, String password, String clientId, String clientSecret)
            throws URISyntaxException, IOException, InterruptedException {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.rateLimitedHttpClient = new RateLimitedHttpClient(allowedRatePerSeconds);
        refreshAccessToken();
    }

    private void refreshAccessToken()
            throws URISyntaxException, IOException, InterruptedException {
        String encodedCredentials = Base64.getEncoder().encodeToString((this.clientId + ":" + this.clientSecret).getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(accessTokenUrl))
                .headers("Authorization", "Basic " + encodedCredentials,
                        "Content-Type", "application/x-www-form-urlencoded",
                        "User-Agent", userAgent)
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=password&username=" + this.username + "&password=" + this.password))
                .build();
        HttpResponse<String> response = this.rateLimitedHttpClient.sendRateLimited(
                request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        this.transientAccessToken = responseJson.get("access_token").getAsString();
    }

    List<Post> getTopPostsFromSubreddit(String subredditName, String timeframe, Integer limit)
            throws URISyntaxException, IOException, InterruptedException {
        List<Post> posts = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/r/" + subredditName + "/top?t=" + timeframe + "&limit=" + limit))
                .headers("Authorization", "Bearer " + this.transientAccessToken,
                        "User-Agent", userAgent)
                .GET()
                .build();
        HttpResponse<String> response = sendRequestWithValidToken(request);

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray postsJson = responseJson.getAsJsonObject("data").get("children").getAsJsonArray();
        for (JsonElement postJson : postsJson) {
            Post post = new Gson().fromJson(postJson.getAsJsonObject().get("data").toString(), Post.class);
            posts.add(post);
        }
        hidePostFromLaterSearches(posts);
        return posts;
    }

    private void hidePostFromLaterSearches(List<Post> posts)
            throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/api/hide?id=" +
                        posts.stream().map(Post::getRedditId).collect(Collectors.joining(","))))
                .headers("Authorization", "Bearer " + this.transientAccessToken,
                        "User-Agent", userAgent)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        sendRequestWithValidToken(request);
    }

    private HttpResponse<String> sendRequestWithValidToken(HttpRequest request)
            throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = this.rateLimitedHttpClient.sendRateLimited(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            refreshAccessToken();
            HttpRequest.Builder newRequestBuilder = HttpRequest.newBuilder()
                    .uri(request.uri())
                    .headers("Authorization", "Bearer " + this.transientAccessToken,
                            "User-Agent", userAgent);
            switch (request.method()) {
                case "POST":
                    request = newRequestBuilder.POST(HttpRequest.BodyPublishers.noBody()).build();
                    break;
                case "GET":
                default:
                    request = newRequestBuilder.GET().build();
                    break;
            }
        }
        return this.rateLimitedHttpClient.sendRateLimited(request, HttpResponse.BodyHandlers.ofString());
    }
}
