package YouTube;

import DiscordBot.BotUtils;
import DiscordBot.Settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.*;

public class NotifyStream extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(NotifyStream.class);

    private static List<Channel> youTubechannels = new ArrayList<>();
    private static List<IChannel> discordChannel = new ArrayList<>();
    private static boolean isTimer = false;
    private static Timer timer;
    static {
        //addYouTubeChannel("UCNrzUrkiCUnb8e0nFpgx8Cw"); // r7ge channel
        //addYouTubeChannel("UCmuKs-oja_1nG4z4Y1NwPqQ"); // yxo channel
        for(String stream : Settings.body.youTubeNotify.stramers){
            addYouTubeChannel(stream);
        }
    }

    public NotifyStream(IGuild guild)
    {
        for(Map.Entry<Long,Long> entry : Settings.body.youTubeNotify.chansForNotify.entrySet())
        {
            if(guild.getLongID() == entry.getValue())
            {
                addDiscordChannel(guild.getChannelByID(entry.getKey()));
                log.info("Added channel "+ guild.getChannelByID(entry.getKey()).getName()+ " to notify");
            }
        }

        RunTimer();
    }

    private void RunTimer()
    {

        if(!isTimer)
        {
            timer = new Timer();
            timer.schedule(this,10000,30000);
            isTimer = true;
            log.debug("Timer start");
        }
    }

    private static void addYouTubeChannel(String id){
        youTubechannels.add(new Channel(id));
    }
    private static void addDiscordChannel(IChannel chan)
    {
        discordChannel.add(chan);
    }
    public void run()
    {
        for(Channel channel :youTubechannels)
        {
            if(channel.isChangeStateToOnline())
            {
                log.info(channel.getVideo().snippet.channelTitle + "is live!!!");
                for(IChannel chan : discordChannel)
                {
                    BotUtils.sendMessage(chan,MessageBuilder.ChanIsLive(channel.getVideo()));
                }
            }
        }
    }

    public void stop(IChannel dscordChan) {

        for (int i = 0; i < discordChannel.size(); i++)
        {
            if(dscordChan == discordChannel.get(i)){
                discordChannel.remove(i);
                i--;
                log.info("Notify fo chan " + dscordChan.getName() + " has been canceled");
                if(discordChannel.size() == 0 && isTimer) {
                    timer.cancel();
                    timer.purge();
                    isTimer = false;
                    log.info("Timer stop");
                }
            }
        }
    }
}
