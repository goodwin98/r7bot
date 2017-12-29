package YouTube;

import DiscordBot.Settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Channel {

    private static final Logger log = LoggerFactory.getLogger(Channel.class);
    private String id;
    private boolean isLive = false;
    private int onLineCount = 0;
    private int offLineCount = 0;
    private YouTubeVideo video;

    Channel(String id){
        this.id = id;
    }


    boolean isChangeStateToOnline() {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + id + "&eventType=live&type=video&key=" + Settings.body.YOUTUBE_KEY;
        if(onLineCount > 0) {
            onLineCount--;

            log.info("Online count - " + onLineCount);

            return false;
        }

        try {
            video = YouTubeRequest.request(url);

            log.info("Status stream - " + video.snippet.liveBroadcastContent);

            if(video.snippet.liveBroadcastContent.compareTo("live") != 0)
            {
                if(offLineCount > 0) {
                    offLineCount--;

                    log.info("Offline count - " + offLineCount);

                    if(offLineCount == 0)
                        isLive = false;
                    return false;
                }

                if(isLive) {
                    offLineCount = 5;
                }
            }
            else if(!isLive) {
                isLive = true;
                onLineCount = 20; // 10 minutes wait
                return true;
            }
            else
            {
                offLineCount = 0; // stream online, and isLive
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
