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

package fredboat.command.serg;

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
import fredboat.util.TextUtils;


public class E9Command extends Command {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("\"file_url\":\"([^\"]+)");
    private static final Pattern IMAGE_LOWRES = Pattern.compile("\"sample_url\":\"([^\"]+)");
    private static final String BASE_URL = "https://www.e926.net/post/index.json?tags=";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(E9Command.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendTyping().queue();

        try {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(BASE_URL);
            stringBuilder.append(args[1]);
            stringBuilder.append(",order:random,&limit=1&login=dotdot20&password_hash=");
            stringBuilder.append(String.valueOf(System.getenv("E926_API")));
            String finalstring = stringBuilder.toString();


            String str = Unirest.get(finalstring).asString().getBody();;
            Matcher m = IMAGE_PATTERN.matcher(str);

            else if (!msize.find()) {
                //channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("e926Fail"), BASE_URL)).queue();
                //log.info("str: " + m);
                log.info("finalsearchstring: " + finalstring);
                //channel.sendMessage(str).queue();
                //channel.sendMessage(finalstring).queue();
                return;
            }

            File tmp = CacheUtil.getImageFromURL(msize.group(1));
            channel.sendFile(tmp, null).queue();
            //log.info("///////////////////" + m.group(1) + "////////////////////////////////");
            //channel.sendMessage(m.group(1)).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (UnirestException e) {
          throw new RuntimeException(e);
        }
        catch (ArrayIndexOutOfBoundsException e) {
          String out = "``` Requires at least one tag from http://www.e926.net/tag/ up to a limit of 3 (due to tag search limits) ```";
          TextUtils.replyWithName(channel, invoker, out);
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Takes in tags and searches on e926.net \n#https://e926.net/tag/index for all available tags";
      }
  }
