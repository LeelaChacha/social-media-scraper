/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper;

import party.hukumraj_singh_deora.social_media_scraper.instagram.InstagramScraper;
import party.hukumraj_singh_deora.social_media_scraper.reddit.RedditScraper;

public class SocialMediaScraperFactory {
    private SocialMediaScraperFactory(){}

    public static RedditScraper getRedditScraper() throws InterruptedException {
        return new RedditScraper();
    }

    public static InstagramScraper getInstagramScraper() {
        return new InstagramScraper();
    }
}
