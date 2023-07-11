package org.welcome.runners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.RichInvite;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InvitesSaveRunner implements Runnable {
    private static final Logger log = LogManager.getLogger(InvitesSaveRunner.class);
    private final Server server;
    private final Map<String, Integer> inviteSaves;
    private boolean stay = true;

    public InvitesSaveRunner(Server server, Map<String, Integer> inviteSaves) {
        this.inviteSaves = inviteSaves;
        this.server = server;
    }

    @Override
    public void run() {

        while (stay) {
            CompletableFuture<Set<RichInvite>> invites = server.getInvites();
            try {
                inviteSaves.clear();
                log.debug("Saving the invites...");
                for (RichInvite invite : invites.get()) {
                    log.debug("Saving {} qty {}", invite.getCode(), invite.getUses());
                    inviteSaves.put(invite.getCode(), invite.getUses());
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception", e);
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                log.error("Exception on sleep", e);
                stay = false;
            }
        }
    }

    public void close() {
        stay = false;
    }
}
