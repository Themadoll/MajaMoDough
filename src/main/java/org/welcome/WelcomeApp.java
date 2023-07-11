package org.welcome;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.welcome.commands.GoodbyeCommand;
import org.welcome.commands.WelcomeCommand;
import org.welcome.runners.InvitesSaveRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class WelcomeApp {
    private static final Logger log = LogManager.getLogger(WelcomeApp.class);

    public static void main(String[] args) {
        Map<String, Integer> inviteSaves = new HashMap<>();
        Properties properties = Utils.readProperties();

        log.info("Starting WelcomeBot");
        String  token = properties.getProperty("welcome.token");

        log.debug("Token={}", token);
        // We log in blocking, just because it is simpler and doesn't matter here
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        log.debug("Adding command listeners");
        // Add listeners
        api.addServerMemberJoinListener(new WelcomeCommand(inviteSaves, properties));
        api.addServerMemberLeaveListener(new GoodbyeCommand(properties));

        log.debug("Adding server listeners");
        // Log a message, if the bot joined or left a server
        api.addServerJoinListener(event -> log.info("Joined server " + event.getServer().getName()));
        api.addServerLeaveListener(event -> log.info("Left server " + event.getServer().getName()));

        log.debug("Starting threads thread");
        Optional<Server> server = api.getServerById(Constants.DEV_SERVER_ID);
        if (server.isPresent()) {
            log.debug("Starting inviteSave thread");
            InvitesSaveRunner invitesRunner = new InvitesSaveRunner(server.get(), inviteSaves);
            Thread invitesSaveThread = new Thread(invitesRunner);
            invitesSaveThread.start();
        }
        log.info("End of WelcomeBot setup");
    }
}
