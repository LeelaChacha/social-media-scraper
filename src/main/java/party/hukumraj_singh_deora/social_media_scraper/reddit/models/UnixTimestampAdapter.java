/*
 * Copyright (c) 2022 Hukumraj Singh Deora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hukumraj Singh Deora <me@hukumraj-singh-deora.party>
 */

package party.hukumraj_singh_deora.social_media_scraper.reddit.models;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

class UnixTimestampAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getTime() / 1000);
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in == null) {
            return null;
        }
        return new Date(in.nextLong() * 1000);
    }

}