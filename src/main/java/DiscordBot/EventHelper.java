package DiscordBot;

import Statistic.StatsMain;
import YouTube.NotifyStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

class EventHelper {
    private static final Logger log = LoggerFactory.getLogger(EventHelper.class);
    private static NotifyStream notify;
    private static HashMap<IGuild, StatsMain> statsList = new HashMap<>();
    private static boolean isTimer = false;
    private static Timer timer = new Timer();


    static StatsMain getStatByGuild(IGuild guild)    {

        return statsList.get(guild);
    }

    static void addGuild(IGuild guild){

        statsList.put(guild, new StatsMain(guild));
        if(!isTimer)
        {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.debug("Start save to DB");
                    for(Map.Entry<IGuild, StatsMain> entry : statsList.entrySet())
                    {
                        entry.getValue().storeToBD();
                    }
                }
            }, 30*60*1000,30*60*1000);
        }
    }
}
