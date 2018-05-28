package DiscordBot;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class Perm {
    public static int ALL = 1;
    public static int FUN = 2;
    public static int MEDIUM = 4;
    public static int HIGHT = 8;
    public static int VHIGHT = 16;

    static int getPermForUser(IUser user, IGuild guild)
    {
        int permition = 0;
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
