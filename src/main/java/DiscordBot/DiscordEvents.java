package DiscordBot;

import YouTube.NotifyStream;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;

import java.util.*;

public class DiscordEvents {


    // A static map of commands mapping from command string to the functional impl
    private static Map<String, Command> commandMap = new HashMap<>();
    static NotifyStream notify;

    // Statically populate the commandMap with the intended functionality
    // Might be better practise to do this from an instantiated objects constructor
    static {


        commandMap.put("testcommand", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), "You ran the test command with args: " + args);
        });

        commandMap.put("ping", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), "pong");
        });

        commandMap.put("exit", (event, args) -> {
            System.exit(0);
        });

        commandMap.put("check", (event, args) -> {
            notify = new NotifyStream(event.getChannel());

        });
        commandMap.put("stop_notify", (event, args) -> {
            notify.stop(event.getChannel());

        });

    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event){


        if(event.getAuthor() != BotUtils.getClient().getUserByID(223528667874197504L)) // bot's owner
            return;

        String[] argArray = event.getMessage().getContent().split(" ");


        if(argArray.length == 0)
            return;


        if(!argArray[0].startsWith(Settings.BOT_PREFIX))
            return;

        String commandStr = argArray[0].substring(1);

        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0); // Remove the command


        if(commandMap.containsKey(commandStr))
            commandMap.get(commandStr).runCommand(event, argsList);
    }

    @EventSubscriber
    public void onGuildCreated(GuildCreateEvent event) { // create or join to guild

    }

    @EventSubscriber
    public void onUserVoiceJoin(UserVoiceChannelJoinEvent event) {

    }

    @EventSubscriber
    public void onUserVoiceLeave(UserVoiceChannelLeaveEvent event) {

    }

    @EventSubscriber
    public void onUserVoiceMove(UserVoiceChannelEvent event){

    }
}
