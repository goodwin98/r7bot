package YouTube.MailNotify;


import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

class MessageBuilder {

    static EmbedObject chanIsLive(YouTubeVideo video)
    {
        EmbedBuilder builder = new EmbedBuilder();


        //builder.withTitle("@everyone, " + video.snippet.channelTitle + " начал стримить!");
        builder.withDescription(video.snippet.title);
        builder.withColor(255,0,0);
        builder.withImage(video.snippet.thumbnails.medium.url);
        //builder.withUrl("https://youtube.com/watch?v=" + video.id);
        builder.setLenient(true);
        builder.appendField("Ссылки:","[YouTube](https://youtube.com/watch?v=" + video.id + ")  |  [YT Gaming](https://gaming.youtube.com/watch?v=" + video.id + ")",false);
        return builder.build();
    }

    static EmbedObject newVideoOnChan(YouTubeVideo video){
        EmbedBuilder builder = new EmbedBuilder();


        //builder.withTitle("@everyone, новое видео на канале " + video.snippet.channelTitle);
        builder.withDescription(video.snippet.title);
        builder.withColor(0,0,255);
        builder.withImage(video.snippet.thumbnails.medium.url);
        //builder.withUrl("https://youtube.com/watch?v=" + video.id);
        builder.setLenient(true);
        builder.appendField("Ссылки:","[YouTube](https://youtube.com/watch?v=" + video.id + ")  |  [YT Gaming](https://gaming.youtube.com/watch?v=" + video.id + ")",false);
        return builder.build();
    }
}
