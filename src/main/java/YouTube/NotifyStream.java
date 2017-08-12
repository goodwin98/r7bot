package YouTube;

import DiscordBot.BotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotifyStream extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(NotifyStream.class);

    private static List<Channel> youTubechannels = new ArrayList<>();
    private static List<IChannel> discordChannel = new ArrayList<>();
    private static boolean isTimer = false;
    private static Timer timer;
    static {
        addYouTubeChannel("UCNrzUrkiCUnb8e0nFpgx8Cw"); // r7ge channel
        //addYouTubeChannel("UCmuKs-oja_1nG4z4Y1NwPqQ"); // yxo channel

    }

    public NotifyStream(IChannel dscrdChan)
    {
        for( IChannel chan : discordChannel)
        {
            if(dscrdChan == chan)
                return;
        }
        addDiscordChannel(dscrdChan);
        log.info("Added channel "+ dscrdChan.getName()+ " to notify");
        RunTimer();
    }

    private void RunTimer()
    {

        if(!isTimer)
        {
            timer = new Timer();
            timer.schedule(this,1000,30000);
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
