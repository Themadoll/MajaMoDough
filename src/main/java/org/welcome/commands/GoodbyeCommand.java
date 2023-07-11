package org.welcome.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.*;
import java.util.List;
import java.util.Properties;

public class GoodbyeCommand implements ServerMemberLeaveListener {
    private static final Logger log = LogManager.getLogger(GoodbyeCommand.class);
    private Server server;
    private final Properties properties;

    public GoodbyeCommand(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void onServerMemberLeave(ServerMemberLeaveEvent event) {
        log.debug("A member is leaving...");
        if (!event.getUser().isBot()) {
            server = event.getServer();
            if (server != null) {
                int memberCount = server.getMemberCount();
                String displayName = event.getUser().getDisplayName(server);
                log.debug("Joined date={}", event.getUser().getJoinedAtTimestamp(server));
                log.debug("Joined id={}", event.getUser().getDiscriminatedName());
                Icon icon = event.getUser().getAvatar();
                int max = 25;
                int min = 1;
                int int_random = (int) Math.floor(Math.random() * (max - min + 1) + min);
                String strBld = "_**Oh no! Somebody has left us:** \n" +
                        properties.getProperty("leaving.message" + int_random) +
                        "_\n\n" +
                        "\n";


                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(Color.decode(properties.getProperty("welcome.border.color")))
                        .setTitle(properties.getProperty("leaving.title").replace("{{user}}", displayName))
                        .setThumbnail(icon)
                        .setDescription(strBld)
                        .setFooter("-------------- Now with " + memberCount + " members! --------------");

                List<ServerTextChannel> channel = server.getTextChannelsByName(properties.getProperty("leaving.channel.name"));
                if (channel != null && !channel.isEmpty()) {
                    channel.get(0).sendMessage(embed)
                            .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                } else {
                    log.error("Channel name is invalid in application.properties");
                }

            }
        }
    }
}