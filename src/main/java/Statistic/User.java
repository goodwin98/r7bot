package Statistic;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

class User {
    private long firstTime;
    private IUser user;

    private IChannel currentChan;

    User(IUser user, IChannel channel)
    {
        firstTime = System.currentTimeMillis();
        this.user = user;
        currentChan = channel;
    }

    long getTime()
    {
        return System.currentTimeMillis() - firstTime;
    }


    IUser getUser() {
        return user;
    }

    IChannel getCurrentChan() {
        return currentChan;
    }

}
