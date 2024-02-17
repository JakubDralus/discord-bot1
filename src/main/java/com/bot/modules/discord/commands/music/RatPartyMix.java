package com.bot.modules.discord.commands.music;

import com.bot.modules.audioplayer.PlayerManager;
import com.bot.modules.discord.commands.ISlashCommand;
import com.bot.modules.spotify.Playlist;
import com.bot.shared.CommandUtil;
import com.bot.shared.CustomPlaylistSettings;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class RatPartyMix implements ISlashCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RatPartyMix.class);
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Playlist.getPlaylistsItems_Async();
        
        AudioChannel userChannel = CommandUtil.getUserVoiceChannel(event);
        AudioChannel botChannel = CommandUtil.getBotVoiceChannel(event);
        
        if (userChannel == null) {
            CommandUtil.replyEmbedErr(event, "Please join a voice channel.");
            return;
        }
        
        if (botChannel == null) {
            CommandUtil.connectToUserChannel(event, userChannel);
            botChannel = userChannel;
        }
        
        if (!Objects.equals(botChannel, userChannel)) {
            CommandUtil.replyEmbedErr(event, "Please be in the same voice channel as the bot.");
            return;
        }
    
        PlayerManager playerManager = PlayerManager.get();
        playerManager.getMusicManager(event.getGuild()).getScheduler().setCommandEvent(event);
        
        int i = 0;
        for (var trackName: Playlist.getTracks().values()) {
            ++i;
//            System.out.println(trackName);
            if (!CustomPlaylistSettings.adjustSong(i, event)) {
                playerManager.play(event.getGuild(), "ytsearch: " + trackName, false, event);
            }
        }
    
        LOGGER.info("used /ratpartymix command in {}", event.getChannel().getName());
    }
}
