package Statistic;

import Statistic.Presence.GameTime;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsMain {

    private DataBase dataBase;
    private Hashtable<IUser,User> users = new Hashtable<>();
    private IGuild currentGuild;

    public StatsMain(IGuild guild){

        currentGuild = guild;
        dataBase = new DataBase();
        for(IVoiceChannel voiceChannel : guild.getVoiceChannels())
        {
            for (IUser iUser:voiceChannel.getConnectedUsers())
            {
                users.put(iUser,new User(iUser,voiceChannel));
            }
        }

    }

    public void userSendMessage(IUser user, IChannel textChan, String message)
    {
        if(user.isBot())
            return;
        if(message.length() > 350 || message.length() < 15)
            return;

        message = message.replaceAll("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]" , " ");
        int count = (message.split("[\\wа-яА-Я]{3,20}").length) / 3;
        if(count > 0) {
            dataBase.saveTextStat(user.getLongID(), textChan.getGuild().getLongID(), textChan.getLongID(), count);
        }
    }

    public void userJoin(IUser user, IVoiceChannel channel)
    {
        if(!users.containsKey(user) && user.getRolesForGuild(currentGuild).size() != 0) {
            users.put(user, new User(user, channel));
        }
    }
    public void userLeave(IUser user)
    {
       if (users.containsKey(user) )
       {
           dataBase.saveVoiceStat(users.get(user),currentGuild.getLongID());
           users.remove(user);
       }
    }

    public void displayTopChannels(IChannel channel, List<String> args)
    {
        int days = 0;
        if(args.size() == 1 ) {
            try {
                days = Integer.parseUnsignedInt(args.get(0));
            } catch (NumberFormatException e)
            {
                //days = 0;
            }
        }
        ResultDataBase base;
        if(days != 0) {
            int minData = DataBase.formatDate(days);
            base = dataBase.getTopChannels(currentGuild.getLongID(),minData);
        } else {
            base = dataBase.getTopChannels(currentGuild.getLongID());
        }
        List<String> result= base.list.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).map((entry) -> {

            StringBuilder row = new StringBuilder();
            if (currentGuild.getVoiceChannelByID(Long.valueOf(entry.getKey())) != null)
                row.append(currentGuild.getVoiceChannelByID(Long.valueOf(entry.getKey())).getName());
            else
                row.append(Long.valueOf(entry.getKey()));
            row.append("\t\t");
            row.append(formatSeconds(entry.getValue()));
            return row.toString();

        }).collect(Collectors.toList());

        channel.sendMessage(MessageBuilder.topChannels(result,toStringFromDate(base.min), toStringFromDate(base.max)));
    }

    public void displayTopUsersByChannels(IChannel channel, List<String> args)
    {

        boolean byGuild = false;
        ResultDataBase base;
        if(args.size() == 1 && args.get(0).length() > 4) {
            if (currentGuild.getVoiceChannelByID(Long.valueOf(args.get(0))) == null) {
                if(currentGuild.getChannelByID(Long.valueOf(args.get(0))) == null) {

                    return;
                }
                displayTopTextUsersByChannel(channel,args,0);
                return;
            }
            base = dataBase.getTopUsersByChannel(currentGuild.getLongID(), args.get(0));
        } else if(args.size() == 2 && args.get(1).length() < 4) {
            if (currentGuild.getVoiceChannelByID(Long.valueOf(args.get(0))) == null) {
                return;
            }
            base = dataBase.getTopUsersByChannel(currentGuild.getLongID(), args.get(0), DataBase.formatDate(Integer.parseInt(args.get(1))));
        } else if(args.size() == 1 && args.get(0).length() < 4) {
            base = dataBase.getTopUsersByGuild(currentGuild.getLongID(), currentGuild.getAFKChannel().getStringID(),DataBase.formatDate(Integer.parseInt(args.get(0))));
            byGuild = true;
        } else if(args.size() == 0) {
            base = dataBase.getTopUsersByGuild(currentGuild.getLongID(), currentGuild.getAFKChannel().getStringID());
            byGuild = true;

        } else {
            return;
        }
        List<String> result= base.list.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).map((entry) -> {

            StringBuilder row = new StringBuilder();
            if (currentGuild.getUserByID(Long.valueOf(entry.getKey())) != null)
                row.append(currentGuild.getUserByID(Long.valueOf(entry.getKey())).getName());
            else
                row.append(Long.valueOf(entry.getKey()));
            row.append("\t\t");
            row.append(formatSeconds(entry.getValue()));
            return row.toString();
        }).collect(Collectors.toList());

        if(!byGuild) {
            channel.sendMessage(MessageBuilder.topUserByChan(result, currentGuild.getVoiceChannelByID(Long.valueOf(args.get(0))).getName(), toStringFromDate(base.min), toStringFromDate(base.max)));
        } else {
            channel.sendMessage(MessageBuilder.topUserByChan(result, currentGuild.getName(), toStringFromDate(base.min), toStringFromDate(base.max)));
        }

    }

    private void displayTopTextUsersByChannel(IChannel channel, List<String> args, int days) {

        ResultDataBase base = dataBase.getTopUsersByChannel(currentGuild.getLongID(),args.get(0));


        List<String> result= base.list.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).map((entry) -> {

            StringBuilder row = new StringBuilder();
            if (currentGuild.getUserByID(Long.valueOf(entry.getKey())) != null)
                row.append(currentGuild.getUserByID(Long.valueOf(entry.getKey())).getName());
            else
                row.append(Long.valueOf(entry.getKey()));
            row.append("\t\t");
            row.append(entry.getValue());
            row.append(" exp");
            return row.toString();
        }).collect(Collectors.toList());


        channel.sendMessage(MessageBuilder.topUserByChan(result, "#" + currentGuild.getChannelByID(Long.valueOf(args.get(0))).getName(), toStringFromDate(base.min), toStringFromDate(base.max)));


    }
    public void displayUserStats(IChannel channel, List<String> args)
    {
        long userId = Long.valueOf(args.get(0));
        if(currentGuild.getUserByID(userId) == null)
        {
            channel.sendMessage("Нет такого пользователя.");
            return;
        }
        ResultUserStat base = dataBase.getUserStat(currentGuild.getLongID(), currentGuild.getAFKChannel().getStringID(), args.get(0));

        StringBuilder aTimeList = new StringBuilder();
        StringBuilder rTimeList = new StringBuilder();
        int i = 1;

        for (Map.Entry<Integer, String> entry : base.allTimeList.entrySet()){

            aTimeList.append(i);
            aTimeList.append(". ");
            if (currentGuild.getVoiceChannelByID(Long.valueOf(entry.getValue())) != null)
                aTimeList.append(currentGuild.getVoiceChannelByID(Long.valueOf(entry.getValue())).getName());
            else
                aTimeList.append(Long.valueOf(entry.getValue()));
            aTimeList.append("\t\t");
            aTimeList.append(formatSeconds(entry.getKey()));
            aTimeList.append("\n");
            i++;
        }
        i = 1;
        for (Map.Entry<Integer, String> entry : base.recentTimeList.entrySet()){
            rTimeList.append(i);
            rTimeList.append(". ");
            if (currentGuild.getVoiceChannelByID(Long.valueOf(entry.getValue())) != null)
                rTimeList.append(currentGuild.getVoiceChannelByID(Long.valueOf(entry.getValue())).getName());
            else
                rTimeList.append(Long.valueOf(entry.getValue()));
            rTimeList.append("\t\t");
            rTimeList.append(formatSeconds(entry.getKey()));
            rTimeList.append("\n");
            i++;
        }
        int lastOnlineDatedate = Statistic.Presence.StatsMain.getLastOnlineDate(userId);
        channel.sendMessage(MessageBuilder.statUser(aTimeList.toString(),rTimeList.toString(), formatSeconds(base.totalTime), toStringFromDate(base.dateMin),
                toStringFromDate(base.dateMax),currentGuild.getUserByID(userId).getName(), toStringFromDate(lastOnlineDatedate)));
    }

    public void displayYesterdayExp(IUser iUser, IGuild iGuild, IChannel iChannel)
    {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        zdt = zdt.minusDays(1);
        int firstDate = Integer.parseInt(DateTimeFormatter.ofPattern("yyyyMMdd").format(zdt));

        int exp = dataBase.getUserExpByGuild(iUser.getLongID(),iGuild.getLongID(),firstDate,firstDate);
        GameTime gameTime = Statistic.Presence.StatsMain.getGameTimeByUser(iUser.getLongID(),firstDate,firstDate );
        String greatMess = ", вчера твоя карма пополнилась всего лишь на ";
        if(iGuild.getLongID() == 349648434266898453L) // для туриста
        {
            greatMess = ", твое уважение к Амычу вчера увеличилось на ";
        }
        if(gameTime.time == 0) {
            iChannel.sendMessage(iUser.getDisplayName(iGuild) + greatMess +
                    exp + " " + rightWord(exp) + " опыта");
        } else {
            iChannel.sendMessage(iUser.getDisplayName(iGuild) + greatMess +
                    exp + " " + rightWord(exp) + " опыта, но зато ты провел " +rightTime(gameTime.time) + " за игрой в " + gameTime.gameName);
        }
    }

    private String rightTime(int seconds)
    {
        if(seconds < 90)
        {
            if(seconds%10 == 1 && (seconds > 20 || seconds < 10))
            {
                return seconds + " секунду";
            } else if ((seconds%10 >= 2 && seconds%10 <= 4) && (seconds > 20 || seconds < 10))
            {
                return seconds + " секунды";
            } else
            {
                return seconds + " секунд";
            }
        } else if (seconds < 6000) {
            if((seconds/60)%10 == 1 && (seconds/60 > 20 || seconds/60 < 10))
            {
                return seconds/60 + " минуту";
            } else if (((seconds/60)%10 >= 2 && (seconds/60)%10 <= 4) && (seconds/60 > 20 || seconds/60 < 10))
            {
                return seconds/60 + " минуты";
            } else
            {
                return seconds/60 + " минут";
            }
        } else {
            if((seconds/3600)%10 == 1 && (seconds/3600 > 20 || seconds/3600 < 10))
            {
                return seconds/3600 + " час";
            } else if (((seconds/3600)%10 >= 2 && (seconds/3600)%10 <= 4) && (seconds/3600 > 20 || seconds/3600 < 10))
            {
                return seconds/3600 + " часа";
            } else
            {
                return seconds/3600 + " часов";
            }
        }
    }
    private String rightWord(int num){
        if(num%10 == 1 && (num > 20 || num < 10))
        {
            return "очко";
        } else if ((num%10 >= 2 && num%10 <= 4) && (num > 20 || num < 10))
        {
            return "очка";
        } else
        {
            return "очков";
        }
    }
    private String formatSeconds (int seconds)
    {
        return String.format(
                "%d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60);
    }

    private static String toStringFromDate(int date)
    {
        try {
            String pattern = "yyyyMMdd";
            DateTimeFormatter parser = DateTimeFormatter.ofPattern(pattern);
            parser.withZone(ZoneId.of("Europe/Moscow"));
            LocalDate dat = LocalDate.parse(Integer.toString(date), parser);
            return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(dat);
        } catch (DateTimeParseException e)
        {
            return "";
        }
    }
}
