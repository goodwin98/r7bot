package Statistic.Presence;

import sx.blah.discord.handle.obj.IUser;

class User {
    private long firstTime;
    private IUser user;

    private String currentGame;

    User(IUser user, String game)
    {
        firstTime = System.currentTimeMillis();
        this.user = user;
        currentGame = game;
    }

    long getTime()
    {
        return System.currentTimeMillis() - firstTime;
    }


    IUser getUser() {
        return user;
    }

    String getCurrentGame() {
        return currentGame;
    }

    void resetTime()
    {
        firstTime = System.currentTimeMillis();
    }
}
