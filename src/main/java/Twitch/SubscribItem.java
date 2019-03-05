package Twitch;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.List;

public class SubscribItem {

    private IGuild guild;
    private IChannel channel;
    private List<String> twChans;

    SubscribItem(IGuild guild, IChannel channel, List<String> twChans)
    {
        this.channel = channel;
        this.guild = guild;
        this.twChans = twChans;
    }

    public IGuild getGuild() {
        return guild;
    }

    public IChannel getChannel() {
        return channel;
    }

    boolean isTwChannelContains(String chan)
    {
        return twChans.contains(chan);
    }
}
