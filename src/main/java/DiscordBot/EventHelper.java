package DiscordBot;

import Statistic.StatsMain;
import YouTube.MailNotify.Subscriber;
import YouTube.NotifyStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class EventHelper {


    private static final Logger log = LoggerFactory.getLogger(EventHelper.class);
    private static NotifyStream notify;
    private static HashMap<IGuild, StatsMain> statsList = new HashMap<>();
    private static Subscriber subscriber = new Subscriber();
    private static ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();


    static StatsMain getStatByGuild(IGuild guild)    {

        return statsList.get(guild);
    }

    static List<StatsMain> getAllStats()
    {
        List<StatsMain> result = new ArrayList<>();
        for (HashMap.Entry<IGuild, StatsMain> item: statsList.entrySet())
        {
            result.add(item.getValue());
        }
        return result;
    }

    static void addGuild(IGuild guild){

        if(!statsList.containsKey(guild))
            statsList.put(guild, new StatsMain(guild));
        //notify = new NotifyStream(guild);

        subscriber.addGuild(guild);

    }
    static void setHourlyTimer(Runnable task)
    {
        ses.scheduleAtFixedRate(task,1,1, TimeUnit.HOURS);
    }

    static void set5MinutesTimer(Runnable task)
    {
        ses.scheduleAtFixedRate(task,5,5, TimeUnit.MINUTES);
    }

}
