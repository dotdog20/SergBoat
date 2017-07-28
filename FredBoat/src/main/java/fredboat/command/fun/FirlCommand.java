/*
 * MIT License
 *
 * Copyright (c) 2016 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    https://e621.net/post/index.xml?tags=sergal,order:random,rating:s&limit=1
 * copies of the Software, and to permit persons to whom the Software is        https://e621.net/post/index.json?tags=sergal,order:random,rating:s&limit=1
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
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.commandmeta.abs.Command;
import fredboat.util.CacheUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fredboat.FredBoat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import fredboat.util.TextUtils;

public class FirlCommand extends Command {

    private static final Logger log = LoggerFactory.getLogger(ArchCommand.class);
    private static final Pattern IMAGE_PATTERN = Pattern.compile("\"url\":\"([^\"]+)");
    private static final String BASE_URL = "https://www.reddit.com/r/furry_irl/random.json?limit=1";

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendTyping().queue();
        try {

          //  response = Unirest.get("http://www.reddit.com/api/v1/access").basicAuth("QjIc95eOrpudxw", "GTLmC3a0-gVbDtuDMUqx4mETezs").asJson();
            String str = Unirest.get(BASE_URL).asString().getBody();
            Matcher m = IMAGE_PATTERN.matcher(str);


            if(!m.find()){
                channel.sendMessage("Failed to extract image from " + BASE_URL).queue();
                channel.sendMessage("INFO: " + str + " " + m).queue();
                return;
            }

            File tmp = CacheUtil.getImageFromURL(m.group(1));
            channel.sendFile(tmp, null).queue();
            log.info("IMG URL:" + m.group(1));
        } catch (UnirestException e) {
            channel.sendMessage("Failed to connect to " + BASE_URL).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
            log.info(e);
        }
    }
}
