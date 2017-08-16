package DiscordBot;

import YouTube.NotifyStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;

import java.util.*;

public class DiscordEvents {
    private static final Logger log = LoggerFactory.getLogger(DiscordEvents.class);

    // A static map of commands mapping from command string to the functional impl
    private static Map<String, Command> commandMap = new HashMap<>();
    private static NotifyStream notify;

    // Statically populate the commandMap with the intended functionality
    // Might be better practise to do this from an instantiated objects constructor
    static {


        commandMap.put("testcommand", (event, args) -> BotUtils.sendMessage(event.getChannel(), "You ran the test command with args: " + args));

        commandMap.put("ping", (event, args) -> BotUtils.sendMessage(event.getChannel(), "pong"));

        commandMap.put("exit", (event, args) -> System.exit(0));

        commandMap.put("check", (event, args) -> notify = new NotifyStream(event.getChannel()));

        commandMap.put("stop_notify", (event, args) -> notify.stop(event.getChannel()));

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

        EventHelper.addGuild(event.getGuild());

    }

    @EventSubscriber
    public void onUserVoiceJoin(UserVoiceChannelJoinEvent event) {

        if(EventHelper.getStatByGuild(event.getGuild()) != null)
            EventHelper.getStatByGuild(event.getGuild()).userJoin(event.getUser(), event.getVoiceChannel());

    }

    @EventSubscriber
    public void onUserVoiceLeave(UserVoiceChannelLeaveEvent event) {
        if(EventHelper.getStatByGuild(event.getGuild()) != null)
            EventHelper.getStatByGuild(event.getGuild()).userLeave(event.getUser(), event.getVoiceChannel());
    }

    @EventSubscriber
    public void onUserVoiceMove(UserVoiceChannelMoveEvent event){
        if(EventHelper.getStatByGuild(event.getGuild()) != null) {
            EventHelper.getStatByGuild(event.getGuild()).userLeave(event.getUser(), event.getOldChannel());
            EventHelper.getStatByGuild(event.getGuild()).userJoin(event.getUser(), event.getNewChannel());
        }
    }
}
