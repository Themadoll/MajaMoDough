package org.welcome.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.RichInvite;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class WelcomeCommand implements ServerMemberJoinListener {
    private static final Logger log = LogManager.getLogger(WelcomeCommand.class);
    private final Map<String, Integer> inviteSaves;
    private Server server;
    private final Properties properties;

    public WelcomeCommand(Map<String, Integer> inviteSaves, Properties properties) {
        this.inviteSaves = inviteSaves;
        this.properties = properties;
    }

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        log.debug("A member is joining...");
        if (!event.getUser().isBot()) {
            server = event.getServer();
            String inviteCode = null;
            String inviteOwnerName = null;
            Optional<User> inviteOwner;
            int memberCount = 0;
            Icon icon = null;
            String displayName = null;
            if (server != null) {
                memberCount = server.getMemberCount();

                displayName = event.getUser().getDisplayName(server);
                log.debug("Joined date={}", event.getUser().getJoinedAtTimestamp(server));
                log.debug("Joined id={}", event.getUser().getDiscriminatedName());
                icon = event.getUser().getAvatar();

                RichInvite invite = findInvite();

                if (invite != null) {
                    inviteCode = invite.getCode();
                    inviteOwnerName = invite.getInviter().get().getDisplayName(server);
                    //inviteOwnerName = "Somebody";
                } else {
                    inviteCode = "No code. We have a wanderer";
                    inviteOwnerName = "Nobody";
                }
            }

            assert server != null;
            List<ServerTextChannel> guidechannel = server.getTextChannelsByName(properties.getProperty("guide.channel.name"));
            String mention = guidechannel.get(0).getMentionTag();

            String strBld = "_**Referred by:** \n" +
                    inviteOwnerName +
                    "   " +
                    "\n **Invite code:** \n" +
                    inviteCode +
                    "_\n\n" +
                    properties.getProperty("welcome.message").replace("{{guide}}", mention) +
                    "\n";


            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.decode(properties.getProperty("welcome.border.color")))
                    .setTitle(properties.getProperty("welcome.title").replace("{{user}}", displayName))
                    .setThumbnail(icon)
                    .setDescription(strBld)
                    .setFooter("-------------- Now with " + memberCount + " members! --------------");

            List<ServerTextChannel> WelcomeChannel = server.getTextChannelsByName(properties.getProperty("welcome.channel.name"));
            if (WelcomeChannel != null && !WelcomeChannel.isEmpty()) {
                WelcomeChannel.get(0).sendMessage(embed)
                        .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            } else {
                log.error("Channel name is invalid in application.properties");
            }
        }
    }


    private RichInvite findInvite() {
        RichInvite foundInvite = null;

        CompletableFuture<Set<RichInvite>> invites = server.getInvites();
        try {
            log.debug("Invites number={}", inviteSaves.size());
            for (RichInvite invite : invites.get()) {
                log.debug("Invite code={}", invite.getCode());

                if (inviteSaves.containsKey(invite.getCode())) {
                    log.debug("Found the invite in the save");
                    int qty = invite.getUses();
                    if (inviteSaves.get(invite.getCode()) < qty) {
                        log.debug("Found the owner");
                        foundInvite = invite;
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("Exception", e);
        } catch (ExecutionException e) {
            log.error("Exception", e);
        }

        return foundInvite;
    }
}
