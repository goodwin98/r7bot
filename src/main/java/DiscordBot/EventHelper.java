package DiscordBot;

import Statistic.StatsMain;
import YouTube.MailNotify.Subscriber;
import YouTube.NotifyStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;
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

    static void addGuild(IGuild guild){

        if(!statsList.containsKey(guild))
            statsList.put(guild, new StatsMain(guild));
        //notify = new NotifyStream(guild);

        subscriber.addGuild(guild);

    }
    static void setDailyTimer(Runnable task)
    {

        //ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        //ZonedDateTime zdtStart = zdt.toLocalDate().atStartOfDay( ZoneId.of("Europe/Moscow") );
        //ZonedDateTime zdtTomorrowStart = zdtStart.plusDays( 1 );
        //zdtTomorrowStart = zdtTomorrowStart.minusMinutes(10);
        //long millis = ChronoUnit.MILLIS.between(zdt,zdtTomorrowStart);
        ses.scheduleAtFixedRate(task,1,1, TimeUnit.HOURS);

    }


}
