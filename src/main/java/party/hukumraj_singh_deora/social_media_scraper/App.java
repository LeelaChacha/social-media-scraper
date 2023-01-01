/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper;

import party.hukumraj_singh_deora.social_media_scraper.reddit.RedditScraper;
import party.hukumraj_singh_deora.social_media_scraper.reddit.enums.RedditDiscoveryStrategies;
import party.hukumraj_singh_deora.social_media_scraper.reddit.models.Post;

import java.util.List;

public class App {
    public static void main(String[] args) throws InterruptedException {
        RedditScraper redditScraper = SocialMediaScraperFactory.getRedditScraper();
        List<Post> posts = redditScraper.scrape("dankmemes", RedditDiscoveryStrategies.MIXED);
        System.out.println(posts);
    }
}
