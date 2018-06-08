package Statistic.Presence;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

public class MessageBuilder {

    static EmbedObject topGames(IChannel channel, List<String> games)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withTitle("Я заметил, что в последнее время народ играет в следующие игры:");
        StringBuilder list = new StringBuilder();

        int count = 0;

        for(String item : games)
        {
            list.append(++count);
            list.append(". ");
            list.append(item);
            list.append("\n");
        }
        if(games.size() == 0)
        {
            list.append("Ой, не знаю");
        }
        builder.appendField("Топ "+ games.size(), list.toString(), true);
        return builder.build();
    }
}
