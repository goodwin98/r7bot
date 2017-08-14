package Statistic;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;

public class StatsMain {

    private DataBase dataBase;
    private HashMap<IUser,User> users = new HashMap<>();

    public StatsMain(IGuild guild){

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
        if(users.containsKey(user))
        {
            users.get(user).join(channel);
        } else {
            users.put(user,new User(user,channel));
        }
    }
    public void userLeave(IUser user, IVoiceChannel channel)
    {
       if (users.containsKey(user))
       {
           users.get(user).leave(channel);
       }
    }
}
