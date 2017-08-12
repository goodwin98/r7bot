package YouTube;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class MessageBuilder {

    public static EmbedObject ChanIsLive(YouTubeVideo video)
    {
        EmbedBuilder builder = new EmbedBuilder();


        builder.withTitle(video.snippet.channelTitle + " начал стримить!");
        builder.withDescription(video.snippet.title);
        builder.withColor(255,0,0);
        builder.withImage(video.snippet.thumbnails.medium.url);
        builder.withUrl("https://gaming.youtube.com/watch?v=" + video.id.videoId);
        return builder.build();
    }
}
