package YouTube;

import DiscordBot.BotUtils;
import DiscordBot.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class Channel {

    private static final Logger log = LoggerFactory.getLogger(Channel.class);
    private String id;
    private boolean isLive = false;
    private YouTubeVideo video;
    private int waitCount = 0;

    Channel(String id){
        this.id = id;
    }


    boolean isChangeStateToOnline() {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + id + "&eventType=live&type=video&key=" + Settings.YOUTUBE_KEY;
        if(waitCount > 0) {
            waitCount--;
            return false;
        }
        try {
            video = YouTubeRequest.request(url);
            if(video.snippet.liveBroadcastContent.compareTo("live") != 0)
            {
                if(isLive) {
                    waitCount = 20;
                    isLive = false;
                }
            }
            else if(!isLive) {
                isLive = true;
                waitCount = 20; // 10 minutes wait
                return true;
            }
        }
        catch (Exception e)
        {
            log.warn("unknowon error", e);

        }
        return false;
    }

    YouTubeVideo getVideo() {
        return video;
    }
}
