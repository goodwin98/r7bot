package DiscordBot;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

class Perm {
    static final int ALL = 1;
    static final int FUN = 2;
    static final int TO_STAT = 4;

    static int getPermForUser(IUser user, IGuild guild)
    {
        int permition = 0;
        if(user.getRolesForGuild(guild).size() != 0)
        {
            permition = permition| TO_STAT;
        }
        for (IRole role : user.getRolesForGuild(guild)) {
            for (Permissions permissions : role.getPermissions()) {
                if (permissions == Permissions.BAN || user.getLongID() == 223528667874197504L) {
                    permition = permition| Perm.ALL;
                }
                if (permissions == Permissions.USE_EXTERNAL_EMOJIS)
                {
                    permition = permition| Perm.FUN;
                }
            }
        }

        return permition;
    }
}
