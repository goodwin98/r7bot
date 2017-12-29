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
        if(list.length() == 0)
        {
            list.append("пусто\n");
        }
        builder.appendField("1 - " + String.valueOf(namesChan.size()),list.toString(), true);

        return builder.build();
    }

    static EmbedObject topUserByChan(List<String> namesUser,String chanForTop, String firstData, String lastData)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withTitle(String.valueOf(namesUser.size()) + " самых активных пользователей в " + chanForTop + ".");
        builder.withDescription("c " + firstData + " по "+ lastData);

        int count = 0;
        int oldCount = 0;
        StringBuilder list = new StringBuilder();
        for(String aNamesUser : namesUser)
        {
            list.append(aNamesUser);
            list.append("\n");
            count++;
            if(count % 20 == 0)
            {
                builder.appendField(String.valueOf(oldCount + 1) +" - " + String.valueOf(count), list.toString(), true);
                list.setLength(0);
                oldCount = count;
            }
        }
        if(count % 20 != 0)
        {
            builder.appendField(String.valueOf(oldCount + 1) +" - " + String.valueOf(count), list.toString(), true);
        }


/*
        for(int i = 0; i > namesUser.size()/20; i++) {
            StringBuilder list = new StringBuilder();
            for (String aNamesUser : namesUser) {
                list.append(aNamesUser);
                list.append("\n");
            }
            builder.appendField("1 - " + String.valueOf(namesUser.size()), list.toString(), true);
        }
*/

        return builder.build();
    }

    static EmbedObject statUser(String aTimeList, String rTimeList, String totalTime, String firstData, String lastData, String userName)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withTitle("Статистика пользователя " + userName);
        builder.withDescription("c " + firstData + " по "+ lastData);

        builder.appendField("Общее время на каналах", totalTime,false);
        builder.appendField("Любимые каналы за всё время", aTimeList, true);
        builder.appendField("Недавние любимые каналы", rTimeList, true);

        return builder.build();
    }
}
