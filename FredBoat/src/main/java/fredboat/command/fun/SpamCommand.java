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

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import net.dv8tion.jda.core.entities.*;

public class SpamCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        VoiceChannel vc = player.getUserCurrentVoiceChannel(invoker);
        player.setCurrentTC(channel);
        try {
            player.joinChannel(vc);
            player.setCurrentTC(channel);
            player.leaveVoiceChannelRequest(channel, false);
            player.joinChannel(vc);
            player.setCurrentTC(channel);
            player.leaveVoiceChannelRequest(channel, false);
            if (vc != null) {
                channel.sendMessage("Joining " + vc.getName())
                        .queue();
            }
        } catch (IllegalStateException ex) {
            if(vc != null) {
                channel.sendMessage("An error occurred. Couldn't join " + vc.getName() + " because I am already trying to connect to that channel. Please try again.")
                        .queue();
            } else {
                throw ex;
            }
        }
    }

}
