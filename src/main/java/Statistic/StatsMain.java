package Statistic;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.Hashtable;

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
        users.put(user,new User(user,channel));
    }
    public void userLeave(IUser user)
    {
       if (users.containsKey(user))
       {
           dataBase.saveTime(users.get(user),currentGuild.hashCode());
           users.remove(user);
       }
    }
}
