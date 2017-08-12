package YouTube;

import DiscordBot.BotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Channel {

    private static final Logger log = LoggerFactory.getLogger(Channel.class);
    private String id;
    private boolean isLive = false;
    private YouTubeVideo video;

    Channel(String id){
        this.id = id;
    }


    public boolean isChangeStateToOnline() {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + id + "&eventType=live&type=video&key=AIzaSyBSDawpBKl-9HvtFeoHDNA083H37DVXJT4";
        try {
            video = YouTubeRequest.request(url);
            if(video.snippet.liveBroadcastContent.compareTo("live") != 0)
            {
                isLive = false;
            }
            else if(isLive == false) {
                isLive = true;

                return true;
            }
        }
        catch (Exception e)
        {
            log.warn("unknowon error", e);

        }
        return false;
    }

    public YouTubeVideo getVideo() {
        return video;
    }
}
