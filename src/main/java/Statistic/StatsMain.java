package Statistic;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class StatsMain {

    private DataBase dataBase;
    private Hashtable<IUser,User> users = new Hashtable<>();
    private IGuild currentGuild;

    public StatsMain(IGuild guild){

        currentGuild = guild;
        dataBase = new DataBase(guild.hashCode());
        for(IVoiceChannel voiceChannel : guild.getVoiceChannels())
        {
            for (IUser iUser:voiceChannel.getConnectedUsers())
            {
                users.put(iUser,new User(iUser,voiceChannel));
            }
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
           dataBase.saveTime(users.get(user),currentGuild.getLongID());
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
                days = 0;
            }
        }
        ResultDataBase base;
        if(days != 0) {
            int minData = DataBase.formatDate(days);
            base = dataBase.getTopChannels(currentGuild.getLongID(),minData);
        } else {
            base = dataBase.getTopChannels(currentGuild.getLongID());
        }
        List<String> result = new ArrayList<>();
        int i = 1;
        for(Map.Entry<Integer,String> entry : base.list.entrySet())
        {
            StringBuilder row = new StringBuilder();
            row.append(i);
            row.append(". ");
            if (currentGuild.getVoiceChannelByID(Long.valueOf(entry.getValue())) != null)
                row.append(currentGuild.getVoiceChannelByID(Long.valueOf(entry.getValue())).getName());
            else
                row.append(Long.valueOf(entry.getValue()));
            row.append("\t\t");
            row.append(formatSeconds(entry.getKey()));
            result.add(row.toString());
            i++;

        }
        channel.sendMessage(MessageBuilder.topChannels(result,String.valueOf(base.min), String.valueOf(base.max)));
    }

    public void displayTopUsersByChannels(IChannel channel, List<String> args)
    {
        boolean byGuild = false;
        ResultDataBase base;
        if(args.size() == 1 && args.get(0).length() > 4) {
            if (currentGuild.getVoiceChannelByID(Long.valueOf(args.get(0))) == null) {
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
        List<String> result = new ArrayList<>();
        int i = 1;
        for (Map.Entry<Integer, String> entry : base.list.entrySet()) {
            StringBuilder row = new StringBuilder();
            row.append(i);
            row.append(". ");
            if (currentGuild.getUserByID(Long.valueOf(entry.getValue())) != null)
                row.append(currentGuild.getUserByID(Long.valueOf(entry.getValue())).getName());
            else
                row.append(Long.valueOf(entry.getValue()));
            row.append("\t\t");
            row.append(formatSeconds(entry.getKey()));
            result.add(row.toString());
            i++;
        }
        if(!byGuild) {
            channel.sendMessage(MessageBuilder.topUserByChan(result, currentGuild.getVoiceChannelByID(Long.valueOf(args.get(0))).getName(), String.valueOf(base.min), String.valueOf(base.max)));
        } else {
            channel.sendMessage(MessageBuilder.topUserByChan(result, currentGuild.getName(), String.valueOf(base.min), String.valueOf(base.max)));
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
}
