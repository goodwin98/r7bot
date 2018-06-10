package DiscordBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BotUtils {
    private static final Logger log = LoggerFactory.getLogger(BotUtils.class);


    private static IDiscordClient cli;

    // Handles the creation and getting of a IDiscordClient object for a token
    static IDiscordClient getBuiltDiscordClient(String token){

        // The ClientBuilder object is where you will attach your params for configuring the instance of your bot.
        // Such as withToken, setDaemon etc
        cli = new ClientBuilder()
                .withToken(token)
                .build();
        return cli;

    }

    // Helper functions to make certain aspects of the bot easier to use.
    static void sendMessage(IChannel channel, String message){

        // This might look weird but it'll be explained in another page.
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
                log.error("Message could not be sent",e);
            }
        });

    }

    public static void sendMessage(IChannel channel, EmbedObject message){
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
                log.error("Message could not be sent",e);
            }
        });

    }

    public static IDiscordClient getClient() {
        return cli;
    }

    static void sendLevelOfUser(IUser user, IMessage message, IChannel channel, IGuild guild)
    {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        if(user.getLongID() == 212594918298877953L)
        {
            channel.sendMessage(user.getDisplayName(guild) + ", теперь, ты мне нравишься всегда, на все свои "  + ((user.getLongID() + zdt.getDayOfMonth()*user.getName().codePointAt(0)) % 101) + " процентов.");
            return;
        } else if (user.getLongID() == 169755338025861122L)
        {
            channel.sendMessage(user.getDisplayName(guild) + ", славься, о богоподобный!");
            return;
        }

        channel.sendMessage(user.getDisplayName(guild) + ", твой писюн сегодня вырос на " + ((user.getLongID()/2 + zdt.getDayOfMonth()*user.getName().codePointAt(0)) % 695-20) + " милиметров." );
    }

}
