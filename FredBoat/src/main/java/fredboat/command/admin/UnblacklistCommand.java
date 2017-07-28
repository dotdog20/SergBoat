/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
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
 */

package fredboat.command.admin;

import fredboat.Config;
import fredboat.command.util.HelpCommand;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommandRestricted;
import fredboat.feature.I18n;
import fredboat.feature.togglz.FeatureFlags;
import fredboat.perms.PermissionLevel;
import fredboat.util.TextUtils;
import fredboat.util.ratelimit.Ratelimiter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;

/**
 * Created by napster on 17.04.17.
 * <p>
 * Lift ratelimit and remove a user from the blacklist
 */
public class UnblacklistCommand extends Command implements ICommandRestricted {
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if (!FeatureFlags.RATE_LIMITER.isActive()) {
            channel.sendMessage("The rate limiter feature has not been turned on.").queue();
            return;
        }

        String command = args[0].substring(Config.CONFIG.getPrefix().length());
        if (message.getMentionedUsers().isEmpty()) {
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        User user = message.getMentionedUsers().get(0);
        String userId = user.getId();

        if (userId == null || "".equals(userId)) {
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        Ratelimiter.getRatelimiter().liftLimitAndBlacklist(user.getIdLong());
        channel.sendMessage(TextUtils.replyWithName(channel, invoker,
                MessageFormat.format(I18n.get(guild).getString("unblacklisted"), user.getAsMention())));
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} @<user>\n#Remove a user from the blacklist.";
    }

    @Override
    public PermissionLevel getMinimumPerms() {
        return PermissionLevel.BOT_OWNER;
    }
}
