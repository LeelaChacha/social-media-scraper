/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper.reddit;

import lombok.extern.slf4j.Slf4j;
import party.hukumraj_singh_deora.social_media_scraper.exceptions.NoCredentialsProvidedException;
import party.hukumraj_singh_deora.social_media_scraper.exceptions.ScrapperError;
import party.hukumraj_singh_deora.social_media_scraper.exceptions.ScrapperInitializationError;
import party.hukumraj_singh_deora.social_media_scraper.reddit.enums.RedditDiscoveryStrategies;
import party.hukumraj_singh_deora.social_media_scraper.reddit.models.Post;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class RedditScraper {
    private final RedditSdk redditSdk;

    public RedditScraper() throws InterruptedException {
        Properties properties = getProperties();
        String redditUsername = properties.getProperty("social_media_scraper.reddit.username");
        String redditPassword = properties.getProperty("social_media_scraper.reddit.password");
        String redditClientId = properties.getProperty("social_media_scraper.reddit.client_id");
        String redditClientSecret = properties.getProperty("social_media_scraper.reddit.client_secret");

        if (redditClientId == null || redditClientSecret == null || redditUsername == null || redditPassword == null)
            throw new NoCredentialsProvidedException("No credentials were provided for the Reddit Scraper." +
                    "Please provide 'social_media_scraper.reddit.username', 'social_media_scraper.reddit.password', " +
                    "'social_media_scraper.reddit.client_id' and 'social_media_scraper.reddit.client_secret' " +
                    "through the application.properties file.");
        try {
            this.redditSdk = new RedditSdk(redditUsername, redditPassword,
                    redditClientId, redditClientSecret);
        } catch (URISyntaxException | IOException e) {
            throw new ScrapperInitializationError(e.getMessage());
        }
    }

    private Properties getProperties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return properties;
    }

    public List<Post> scrape(String pageToScrape, RedditDiscoveryStrategies discoveryStrategy)
            throws ScrapperError, InterruptedException {
        try {
            switch (discoveryStrategy) {
                case IMAGES:
                    return Stream.concat(
                            this.redditSdk.getTopPostsFromSubreddit(pageToScrape, "week", 50).stream(),
                            this.redditSdk.getTopPostsFromSubreddit(pageToScrape, "all", 10).stream()
                    ).collect(Collectors.toList());
                case VIDEOS:
                    return Stream.concat(
                            this.redditSdk.getTopPostsFromSubreddit(pageToScrape, "week", 30).stream(),
                            this.redditSdk.getTopPostsFromSubreddit(pageToScrape, "all", 5).stream()
                    ).collect(Collectors.toList());
                case MIXED:
                    return Stream.concat(
                            this.redditSdk.getTopPostsFromSubreddit(pageToScrape, "week", 40).stream(),
                            this.redditSdk.getTopPostsFromSubreddit(pageToScrape, "all", 8).stream()
                    ).collect(Collectors.toList());
            }
        } catch (URISyntaxException | IOException e) {
            throw new ScrapperError(e.getMessage());
        }
        return Collections.emptyList();
    }
}
