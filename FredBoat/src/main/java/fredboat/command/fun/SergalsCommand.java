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
import fredboat.commandmeta.abs.ICommandOwnerRestricted;
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

public class SergalsCommand extends Command implements ICommandOwnerRestricted {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("\"file_url\":\"([^\"]+)");
    private static final String BASE_URL = "https://www.e621.net/post/index.json?tags=sergal,order:random,rating:e&limit=1/";

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendTyping().queue();
        try {
            String str = Unirest.get(BASE_URL).asString().getBody();
            Matcher m = IMAGE_PATTERN.matcher(str);

            if(!m.find()){
                channel.sendMessage("Failed to extract image from " + BASE_URL).queue();
                channel.sendMessage("INFO:" + str + m).queue();
                return;
            }
            if(invoker.isUserBotCommander());
              File tmp = CacheUtil.getImageFromURL(m.group(1));
              channel.sendFile(tmp, null).queue();
              else
                channel.sendMessage("GIT-PUSH - 85f4hj5").queue();
        } catch (UnirestException e) {
            channel.sendMessage("Failed to connect to " + BASE_URL).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
