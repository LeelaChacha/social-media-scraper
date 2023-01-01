/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper.http;

import com.google.common.util.concurrent.RateLimiter;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RateLimitedHttpClient {
    private final RateLimiter rateLimiter;

    private final HttpClient httpClient;

    public RateLimitedHttpClient(double allowedRequestsPerSecond) {
        this.rateLimiter = RateLimiter.create(allowedRequestsPerSecond);
        this.httpClient = HttpClient.newHttpClient();
    }

    public <T> HttpResponse<T> sendRateLimited(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler)
            throws IOException, InterruptedException {
        this.rateLimiter.acquire();
        return this.httpClient.send(request, bodyHandler);
    }
}
