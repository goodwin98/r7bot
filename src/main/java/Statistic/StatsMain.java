package Statistic;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.Hashtable;
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
           //storeToBD();
       }
    }
    public void storeToBD(){

        for(Map.Entry<IUser,User> entry : users.entrySet())
        {
            entry.getValue().fixState();
            dataBase.saveUser(entry.getValue());
        }
        users.clear();
        for(IVoiceChannel voiceChannel : currentGuild.getVoiceChannels())
        {
            for (IUser iUser:voiceChannel.getConnectedUsers())
            {
                users.put(iUser,new User(iUser,voiceChannel));
            }
        }
    }
}
