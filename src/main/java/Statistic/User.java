package Statistic;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;

class User {
    private long firstTime;
    private IUser user;
    private HashMap<IChannel,Long> allTime = new HashMap<>();
    private IChannel currentChan;

    User(IUser user, IChannel channel)
    {
        firstTime = System.currentTimeMillis();
        this.user = user;
        currentChan = channel;
    }

    void join(IChannel chan)
    {
        firstTime = System.currentTimeMillis();
        currentChan = chan;
    }

    private void fixState()
    {
        long thisTime = System.currentTimeMillis() - firstTime;
        if(allTime.containsKey(currentChan))
        {
            allTime.replace(currentChan,allTime.get(currentChan) + thisTime);
        } else {
            allTime.put(currentChan,thisTime);
        }
    }

    void leave(IChannel chan)
    {
        if( currentChan != chan)
        {
            currentChan = null;
        }
        else {
            fixState();
        }
    }


    IUser getUser() {
        return user;
    }

    IChannel getCurrentChan() {
        return currentChan;
    }
}
