package DiscordBot;

import Statistic.StatsMain;
import YouTube.NotifyStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;

class EventHelper {
    private static final Logger log = LoggerFactory.getLogger(EventHelper.class);
    private static NotifyStream notify;
    private static HashMap<IGuild, StatsMain> statsList = new HashMap<>();



    static StatsMain getStatByGuild(IGuild guild)    {

        return statsList.get(guild);
    }

    static void addGuild(IGuild guild){

        statsList.put(guild, new StatsMain(guild));
    }
}
