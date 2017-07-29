/*
 * MIT License
 *
 * Copyright (c) 2016 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fredboat.command.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.Config;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IFunCommand;
import fredboat.util.rest.CacheUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fredboat.FredBoat;
import fredboat.feature.I18n;

import org.slf4j.LoggerFactory;

import fredboat.util.rest.CloudFlareScraper;

public class E9Command extends Command {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("file_url=\"([^\"]+)");
    private static final String BASE_URL = "https://www.e926.net/post/index.json?tags=-spread_legs,-breasts,order:random,rating:s,";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(E9Command.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendTyping().queue();
        FredBoat.executor.submit(() -> postE9(guild, channel));
    }

    private void postE9(Guild guild, TextChannel channel) {
        try {
            String str = CloudFlareScraper.get(BASE_URL);
            Matcher m = IMAGE_PATTERN.matcher(str);

            if (!m.find()) {
                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("e926Fail"), BASE_URL)).queue();
                log.info(str);
                log.info(BASE_URL);
                return;
            }

            File tmp = CacheUtil.getImageFromURL(m.group(1));
            channel.sendFile(tmp, null).queue();
            log.info(m.group(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Takes in tags and searches on e926.net";
      }
  }
