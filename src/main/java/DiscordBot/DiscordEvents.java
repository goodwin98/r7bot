package DiscordBot;

import DiscordBot.Settings.Settings;
import Statistic.Presence.StatsMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;

import java.util.*;



public class DiscordEvents {
    private static final Logger log = LoggerFactory.getLogger(DiscordEvents.class);

    // A static map of commands mapping from command string to the functional impl
    private static Map<String, Command> commandMap = new HashMap<>();
    private static Map<String, Command> commandMapFun = new HashMap<>();

    
    //private static NotifyStream notify;

    // Statically populate the commandMap with the intended functionality
    // Might be better practise to do this from an instantiated objects constructor
    static {


        commandMap.put("testcommand", (event, args) -> BotUtils.sendMessage(event.getChannel(), "You ran the test command with args: " + args));

        commandMap.put("ping", (event, args) -> BotUtils.sendMessage(event.getChannel(), "pong"));

        commandMap.put("exit", (event, args) -> System.exit(0));


        //commandMap.put("stop_notify", (event, args) -> notify.stop(event.getChannel()));

        commandMap.put("top_chans", (event, args) -> EventHelper.getStatByGuild(event.getGuild()).displayTopChannels(event.getChannel(),args));

        commandMap.put("top_users", (event, args) -> EventHelper.getStatByGuild(event.getGuild()).displayTopUsersByChannels(event.getChannel(),args));

        commandMap.put("user_stat", (event, args) -> EventHelper.getStatByGuild(event.getGuild()).displayUserStats(event.getChannel(),args));

        commandMap.put("save_statistic",  (event, args) -> StatsMain.resetStat());

        commandMap.put("top_games", (event, args) -> StatsMain.displayToGames(event.getChannel()));

        commandMapFun.put("level", (event, args) -> EventHelper.getStatByGuild(event.getGuild()).displayYesterdayExp(event.getAuthor(),event.getGuild(),event.getChannel()));
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event){

        int permission = Perm.getPermForUser(event.getAuthor(),event.getGuild());
        if(permission == 0)
            return;


        if((permission & Perm.TO_STAT) != 0){
            if(EventHelper.getStatByGuild(event.getGuild()) != null)
                EventHelper.getStatByGuild(event.getGuild()).userSendMessage(event.getAuthor(),event.getChannel(),event.getMessage().getContent());
        }


        String[] argArray = event.getMessage().getContent().split(" +");


        if(argArray.length == 0)
            return;


        if(!argArray[0].startsWith(Settings.body.BOT_PREFIX))
            return;

        String commandStr = argArray[0].substring(1);

        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0); // Remove the command

        if((permission & Perm.ALL) != 0) {
            if (commandMap.containsKey(commandStr))
                commandMap.get(commandStr).runCommand(event, argsList);
        }
        if((permission & (Perm.FUN | Perm.ALL)) != 0) {
            if (commandMapFun.containsKey(commandStr))
                commandMapFun.get(commandStr).runCommand(event, argsList);
        }

    }

    @EventSubscriber
    public void onGuildCreated(GuildCreateEvent event) { // create or join to guild

        EventHelper.addGuild(event.getGuild());
        StatsMain.createGuild(event.getGuild());
        log.info("Guild " + event.getGuild().getName() + " created");


    }

    @EventSubscriber
    public void onUserVoiceJoin(UserVoiceChannelJoinEvent event) {

        if(EventHelper.getStatByGuild(event.getGuild()) != null)
            EventHelper.getStatByGuild(event.getGuild()).userJoin(event.getUser(), event.getVoiceChannel());


    }

    @EventSubscriber
    public void onUserVoiceLeave(UserVoiceChannelLeaveEvent event) {
        if(EventHelper.getStatByGuild(event.getGuild()) != null)
            EventHelper.getStatByGuild(event.getGuild()).userLeave(event.getUser());

    }

    @EventSubscriber
    public void onUserVoiceMove(UserVoiceChannelMoveEvent event){
        if(EventHelper.getStatByGuild(event.getGuild()) != null) {
            EventHelper.getStatByGuild(event.getGuild()).userLeave(event.getUser());
            EventHelper.getStatByGuild(event.getGuild()).userJoin(event.getUser(), event.getNewChannel());
        }
    }

    @EventSubscriber
    public void onPresenceUpdate(PresenceUpdateEvent event){
        StatsMain.updateUserPresence(event.getUser(), event.getOldPresence());
    }

}
