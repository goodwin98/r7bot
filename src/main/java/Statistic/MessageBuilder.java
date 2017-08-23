package Statistic;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

class MessageBuilder {

    static EmbedObject topChannels(List<String> namesChan, String firstData, String lastData)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withTitle("Список самых популярных каналов.");
        builder.withDescription("c " + firstData + " по "+ lastData);
        StringBuilder list = new StringBuilder();
        for (String aNamesChan : namesChan) {
            list.append(aNamesChan);
            list.append("\n");
        }
        builder.appendField("1 - " + String.valueOf(namesChan.size()),list.toString(), true);

        return builder.build();
    }

    static EmbedObject topUserByChan(List<String> namesUser,String chanForTop, String firstData, String lastData)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withTitle("20 самых активных пользователей в " + chanForTop + ".");
        builder.withDescription("c " + firstData + " по "+ lastData);
        StringBuilder list = new StringBuilder();
        for (String aNamesUser : namesUser) {
            list.append(aNamesUser);
            list.append("\n");
        }
        builder.appendField("1 - " + String.valueOf(namesUser.size()),list.toString(), true);

        return builder.build();
    }
}
