package YouTube.MailNotify;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.List;

public class SubscribItem {
    private IGuild guild;
    private IChannel channel;
    private List<String> YtChans;

    SubscribItem(IGuild guild, IChannel channel, List<String> ytChans)
    {
        this.channel = channel;
        this.guild = guild;
        this.YtChans = ytChans;
    }

    public IGuild getGuild() {
        return guild;
    }

    public IChannel getChannel() {
        return channel;
    }

    boolean isYtChannelContains(String chan)
    {
        return YtChans.contains(chan);
    }
}
