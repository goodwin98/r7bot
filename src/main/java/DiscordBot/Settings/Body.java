package DiscordBot.Settings;

import java.util.ArrayList;
import java.util.List;

public class Body {
    public String BOT_TOKEN;
    public String BOT_PREFIX;
    public String YOUTUBE_KEY;
    public String   IMAP_AUTH_EMAIL;
    public String   IMAP_AUTH_PWD;
    public String   IMAP_Server;
    public String   IMAP_Port;
    public Statistic statistic = new Statistic();
    public YouTubeNotify youTubeNotify = new YouTubeNotify();

    public List<YouTubeSubscribeItem> youTubeSubscribeItemList = new ArrayList<>();

}
