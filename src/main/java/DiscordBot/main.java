package DiscordBot;

import DiscordBot.Settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class main {
    private static final Logger log = LoggerFactory.getLogger(main.class);


    public static void main(String[] args) {

        if(!Settings.load())
            System.exit(1);
        IDiscordClient cli = BotUtils.getBuiltDiscordClient(Settings.body.BOT_TOKEN);


        // Register a listener via the EventSubscriber annotation which allows for organisation and delegation of events
        cli.getDispatcher().registerListener(new DiscordEvents());

        // Only login after all events are registered otherwise some may be missed.
        try {

            cli.login();
        }
        catch(DiscordException e) {
            //System.err.println("Ошибка при подключении бота к Discord: " + e.getMessage());
            log.error("Error connect to discord",e);

            System.exit(1);
        }

    }

}
