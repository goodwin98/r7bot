package Twitch;

import DiscordBot.Settings.Settings;
import DiscordBot.Settings.TwitchChannel;
import DiscordBot.Settings.TwitchSubscribeItem;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    private List<SubscribItem> subscribItems = new ArrayList<>();

    public void addGuild(IGuild guild)
    {

        for(SubscribItem subscribItem: subscribItems)
        {
            if(subscribItem.getGuild().equals(guild))
            {
                return;
            }
        }
        for(TwitchSubscribeItem settingsItem: Settings.body.twitchSubscribeItems)
        {
            if(settingsItem.guild == guild.getLongID() && guild.getChannelByID(settingsItem.channel) != null)
            {
                List<String> a = new ArrayList<>();
                for( TwitchChannel settingTWChans : settingsItem.twChans)
                {

                    a.add(twitchAPI.getUserIDbyName(settingTWChans.twChanName));
                }
                SubscribItem item = new SubscribItem(guild,guild.getChannelByID(settingsItem.channel), a);
                subscribItems.add(item);
                log.info("TWSubscribe settings for guild " + guild.getName() + " has been added");
            }
        }
    }
    public void CheckAndUpdateSubs()
    {
        for (TwitchSubscribeItem item : Settings.body.twitchSubscribeItems)
        {
            for (TwitchChannel channel: item.twChans)
            {
                if(channel.expire_time.isBefore(Instant.now()))
                {
                    twitchAPI.SubsToChangeStateStreamByName(channel.twChanName);
                    channel.expire_time = Instant.now().plusSeconds(864000 - 24*3600);
                    Settings.store();
                }
            }
        }
    }

    void NewLiveStreamEvent(InputStream inputStream)
    {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(inputStream);
        StreamInfoData streamInfoData = gson.fromJson(reader,StreamInfoData.class);
        for (StreamInfo streamInfo: streamInfoData.data)
        {
            for (SubscribItem item: subscribItems)
            {
                if (item.isTwChannelContains(streamInfo.user_id))
                {
                    String name = twitchAPI.getUserNameById(streamInfo.user_id);
                    log.info("new twitch streamer is live");
                    streamInfo.user_name = name;
                    item.getChannel().sendMessage("@everyone, " + name + " начал стримить! ",chanIsLive(streamInfo));
                }
            }
        }
    }
    private static EmbedObject chanIsLive(StreamInfo streamInfo)
    {
        EmbedBuilder builder = new EmbedBuilder();


        //builder.withTitle("@everyone, " + video.snippet.channelTitle + " начал стримить!");
        builder.withDescription(streamInfo.title);
        builder.withColor(255,0,0);
        String thumUrl = streamInfo.thumbnail_url.replace("{width}","320");
        thumUrl = thumUrl.replace("{height}","180");
        builder.withImage(thumUrl + "?r=" + (int) (Math.random() * 100000));
        //builder.withUrl("https://youtube.com/watch?v=" + video.id);
        builder.setLenient(true);
        builder.appendField("Ссылки:","[Twitch](https://www.twitch.tv/" + streamInfo.user_name +")",false);
        return builder.build();
    }
}
