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
        if(user.isBot())
            return;
        if(presence.getText().isPresent() && presence.getActivity().isPresent() && presence.getActivity().get() == ActivityType.PLAYING){
            if(!users.containsKey(user))
            {
                users.put(user, new User(user,presence.getText().get()));
            } else {
                dataBase.savePresenceStat(users.get(user));
                users.replace(user,new User(user,presence.getText().get()));
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
                if (!iuser.isBot() && iuser.getPresence().getStatus() != StatusType.OFFLINE && !iuser.getPresence().getStreamingUrl().isPresent()) {
                    if(iuser.getPresence().getText().isPresent() && !users.containsKey(iuser))
                    {
                        users.put(iuser, new User(iuser,iuser.getPresence().getText().get()));
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
        log.info("Start saving all stats to database");
        Hashtable<IUser,User> users_copy = new Hashtable<>(users);
        for(Map.Entry<IUser,User> entry : users_copy.entrySet())
        {
            dataBase.savePresenceStat(entry.getValue());
            try {

                users.get(entry.getKey()).resetTime();

            } catch (Exception e) {
                log.error("Error with saving all stats");
            }
        }
        log.info("Finish saving all stats to database");
    }

    public static void resetStat(IUser iUser)
    {
        if(users.containsKey(iUser))
        {
            dataBase.savePresenceStat(users.get(iUser));
            users.get(iUser).resetTime();
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

    public static GameTime getGameTimeByUser(long user, int firstDate, int lastDate)
    {
        return dataBase.getGameTimeForUser(user,firstDate,lastDate);
    }
}
