package YouTube;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.util.EmbedBuilder;

class MessageBuilder {

    static EmbedObject ChanIsLive(YouTubeVideo video)
    {
        EmbedBuilder builder = new EmbedBuilder();


        builder.withTitle(video.snippet.channelTitle + " начал стримить! :r7geEyes:");
        builder.withDescription(video.snippet.title);
        builder.withColor(255,0,0);
        builder.withImage(video.snippet.thumbnails.medium.url);
        builder.withUrl("https://youtube.com/watch?v=" + video.id.videoId);
        builder.setLenient(true);
        builder.appendField("Ссылки:","[YouTube](https://youtube.com/watch?v=" + video.id.videoId + ")\t\t[YT Gaming](https://gaming.youtube.com/watch?v=" + video.id.videoId + ")",false);
        return builder.build();
    }
}
