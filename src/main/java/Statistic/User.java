package Statistic;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

class User {
    private long firstTime;
    private long firstTimeToExp;
    private IUser user;
    private boolean isPaused = false;
    private long pausedTime;

    private IChannel currentChan;

    User(IUser user, IChannel channel)
    {
        firstTime = System.currentTimeMillis();
        this.user = user;
        currentChan = channel;
        firstTimeToExp = firstTime;
    }

    long getTime()
    {
        return System.currentTimeMillis() - firstTime;
    }

    void setTimeToExp()
    {
        firstTimeToExp = System.currentTimeMillis();
    }

    void pauseExpTime()
    {
        if(!isPaused) {
            isPaused = true;
            pausedTime = System.currentTimeMillis() - firstTimeToExp;
        }
    }

    void resumeExpTime()
    {
        isPaused = false;
        firstTimeToExp = System.currentTimeMillis() - pausedTime;
    }
    long getExpTime()
    {
        if(isPaused)
        {
            return pausedTime;
        }
        return System.currentTimeMillis() - firstTimeToExp;
    }
    IUser getUser() {
        return user;
    }

    IChannel getCurrentChan() {
        return currentChan;
    }

}
