package Statistic.Presence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class StatsMain {
    private static final Logger log = LoggerFactory.getLogger(Statistic.Presence.StatsMain.class);

    private static DataBase dataBase = new DataBase();
    private static Hashtable<IUser,User> users = new Hashtable<>();

    public static void updateUserPresence(IUser user, IPresence presence)
    {
        if(presence.getPlayingText().isPresent()){
            if(!users.containsKey(user))
            {
                users.put(user, new User(user,presence.getPlayingText().get()));
            } else {
                dataBase.savePresenceStat(users.get(user));
                users.replace(user,new User(user,presence.getPlayingText().get()));
            }
        } else {
            if (users.containsKey(user))
            {
                dataBase.savePresenceStat(users.get(user));
                users.remove(user);
            } else {
                dataBase.updateUserLastDate(user.getLongID());
            }
        }
    }

    public static void createGuild(IGuild guild)
    {
        List<Long> listUsers = new ArrayList<>();
            for (IUser iuser : guild.getUsers()) {
                if (!iuser.isBot() && iuser.getPresence().getStatus() != StatusType.OFFLINE) {
                    if(iuser.getPresence().getPlayingText().isPresent() && !users.containsKey(iuser))
                    {
                        users.put(iuser, new User(iuser,iuser.getPresence().getPlayingText().get()));
                    } else {
                        //dataBase.updateUserLastDate(iuser.getLongID());
                        listUsers.add(iuser.getLongID());
                    }
                }
            }
            dataBase.updateUsersLastDate(listUsers);

    }

    public static void resetStat()
    {
        Hashtable<IUser,User> users_copy = users;
        for(Map.Entry<IUser,User> entry : users_copy.entrySet())
        {
            dataBase.savePresenceStat(entry.getValue());
            try {
                if(entry.getKey().getPresence().getPlayingText().isPresent())
                    users.replace(entry.getKey(), new User(entry.getKey(), entry.getKey().getPresence().getPlayingText().get()));
            } catch (Exception e) {
                log.error("Error with saving stats");
            }
        }
    }

    public static void displayToGames(IChannel channel)
    {
        EmbedObject message = MessageBuilder.topGames(channel,dataBase.getTopGames());
        channel.sendMessage(message);
    }

    public static int getLastOnlineDate(long user)
    {
        return dataBase.getLastDateOnline(user);
    }
}
