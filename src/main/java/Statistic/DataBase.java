package Statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.TreeMap;


class DataBase {
    private static final Logger log = LoggerFactory.getLogger(DataBase.class);

    private Statement statement;
    private Connection connection;


    DataBase(int serverID)
    {
        statement = ConnectDB();
        //TODO исключение при создании БД

        try {
            createTables();
        }catch ( SQLException e){
            log.error("DataBase not create",e);
        }

    }

    private Statement ConnectDB()
    {
        connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:statistics.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.


        }
        catch (SQLException e)
        {
            log.error("Error in create database",e);
        }
        catch ( ClassNotFoundException e)
        {
            log.error("Class DataBase not found",e);
        }

        return statement;

    }
    private void createTables() throws SQLException
    {

        String sqlCreateChannels = "CREATE TABLE IF NOT EXISTS channels (" +
                "    id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    ChanID STRING  UNIQUE" +
                "                   NOT NULL," +
                "    Guild  STRING NOT NULL" +
                ");";
        String sqlCreateUserChan = "CREATE TABLE IF NOT EXISTS UserChan (" +
                "    id      INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    UserID  STRING  NOT NULL," +
                "    channel INTEGER NOT NULL" +
                ");";
        String sqlCreateStats = "CREATE TABLE IF NOT EXISTS Stats (" +
                "    id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    Data     INTEGER NOT NULL," +
                "    userchan INTEGER NOT NULL," +
                "    Seconds  INTEGER NOT NULL" +
                ");";

        statement.executeUpdate(sqlCreateChannels);
        statement.executeUpdate(sqlCreateUserChan);
        statement.executeUpdate(sqlCreateStats);

    }

    private int getChannel(String channel, long guild) throws SQLException
    {
        String sqlSelect = "SELECT id FROM channels WHERE ChanID = '%s';";
        String sqlInsert = "INSERT INTO channels (ChanID, Guild) VALUES ('%s', '%s');";

        ResultSet row = statement.executeQuery(String.format(sqlSelect,channel));
        if(!row.next())
        {
            statement.executeUpdate(String.format(sqlInsert,channel,Long.toString(guild)));
            row = statement.executeQuery(String.format(sqlSelect,channel));
        }
        return row.getInt("id");

    }
    private int getUserChan(String channel, long guild, String UserID) throws SQLException {

        String sqlSelect = "SELECT id FROM UserChan WHERE UserID = '%s' AND channel = %d;";
        String sqlInsert = "INSERT INTO UserChan (UserID, channel) VALUES ('%s',%d);";

        int chan_id = getChannel(channel,guild);
        ResultSet row = statement.executeQuery(String.format(sqlSelect,UserID,chan_id));
        if(!row.next())
        {
            statement.executeUpdate(String.format(sqlInsert,UserID,chan_id));
            row = statement.executeQuery(String.format(sqlSelect,UserID,chan_id));
        }
        return row.getInt("id");
    }
    void saveTime (User us, long guild)
    {
        String sqlSelect = "SELECT id, Seconds FROM Stats WHERE Data = %d AND userchan = %d;";
        String sqlInsert = "INSERT INTO Stats (Data, userchan, Seconds) VALUES (%d, %d, %d);";
        String sqlUpdate = "UPDATE Stats SET Seconds = %d WHERE id = %d;";

        try {
            int userchan_id = getUserChan(us.getCurrentChan().getStringID(), guild, us.getUser().getStringID());
            Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));
            LocalDate ld = LocalDate.now(clock);
            int nowData = Integer.parseInt(DateTimeFormatter.ofPattern("yyyyMMdd").format(ld));
            ResultSet row = statement.executeQuery(String.format(sqlSelect, nowData,userchan_id));
            if(!row.next())
            {
                statement.executeUpdate(String.format(sqlInsert,nowData,userchan_id,us.getTime()/1000));
            } else {
                int oldSeconds = row.getInt("Seconds");
                int oldId = row.getInt("id");
                statement.executeUpdate(String.format(sqlUpdate,oldSeconds + us.getTime()/1000, oldId));

            }

        } catch (SQLException e) {
            log.error("Error save to dataBase" ,e);
        }
    }
    ResultDataBase getTopChannels(long guild){

        String sqlSelect = "SELECT ChanID AS first_columnn, SUM(Seconds), MIN(Data), MAX(Data) FROM Stats JOIN " +
                "UserChan ON Stats.userchan = UserChan.id JOIN " +
                "channels ON UserChan.channel = channels.id WHERE Guild = %s GROUP BY ChanID ORDER BY SUM(Seconds) DESC;";

        return getTopSecondFromDB(String.format(sqlSelect, Long.toString(guild)));
    }

    ResultDataBase getTopChannels(long guild, int minData){

        String sqlSelect = "SELECT ChanID AS first_columnn, SUM(Seconds), MIN(Data), MAX(Data) FROM Stats JOIN " +
                "UserChan ON Stats.userchan = UserChan.id JOIN " +
                "channels ON UserChan.channel = channels.id WHERE Guild = %s AND Data > %d GROUP BY ChanID ORDER BY SUM(Seconds) DESC;";

        return getTopSecondFromDB(String.format(sqlSelect, Long.toString(guild), minData));
    }

    ResultDataBase getTopUsersByChannel (long guild, String chan_to_top)
    {
        String sqlSelect = "SELECT UserID AS first_columnn, SUM(Seconds),  MIN(Data), MAX(Data) FROM Stats JOIN " +
                "UserChan ON Stats.userchan = UserChan.id JOIN " +
                "channels ON UserChan.channel = channels.id " +
                "WHERE Guild = '%s' AND ChanId = '%s' GROUP BY UserID ORDER BY SUM(Seconds) DESC LIMIT 20;";


        return getTopSecondFromDB(String.format(sqlSelect, Long.toString(guild), chan_to_top));
    }

    ResultDataBase getTopUsersByGuild(long guild)
    {
        String sqlSelect = "SELECT UserID AS first_columnn, SUM(Seconds),  MIN(Data), MAX(Data) FROM Stats JOIN " +
                "UserChan ON Stats.userchan = UserChan.id JOIN " +
                "channels ON UserChan.channel = channels.id " +
                "WHERE Guild = '%s' GROUP BY UserID ORDER BY SUM(Seconds) DESC LIMIT 20;";

        return getTopSecondFromDB(String.format(sqlSelect, Long.toString(guild)));

    }
    ResultDataBase getTopUsersByGuild(long guild, String AFKChannel)
    {
        String sqlSelect = "SELECT UserID AS first_columnn, SUM(Seconds),  MIN(Data), MAX(Data) FROM Stats JOIN " +
                "UserChan ON Stats.userchan = UserChan.id JOIN " +
                "channels ON UserChan.channel = channels.id " +
                "WHERE Guild = '%s' AND ChanId != '%s'" +
                "GROUP BY UserID ORDER BY SUM(Seconds) DESC LIMIT 20;";

        return getTopSecondFromDB(String.format(sqlSelect, Long.toString(guild), AFKChannel));

    }

    private ResultDataBase getTopSecondFromDB(String sqlSelect) {

        ResultDataBase result = new ResultDataBase();
        result.list = new TreeMap<>(Collections.reverseOrder());
        result.max = 0;
        result.min = 0;
        try {
            ResultSet row = statement.executeQuery(sqlSelect);
            while(row.next())
            {
                result.list.put(row.getInt("SUM(Seconds)"),row.getString("first_columnn"));
                if(row.getInt("MAX(Data)") > result.max  || result.max == 0){
                    result.max = row.getInt("MAX(Data)");
                }
                if (row.getInt("MIN(Data)") < result.min || result.min == 0 ) {
                    result.min = row.getInt("MIN(Data)");
                }
            }
        } catch (SQLException e) {
            log.error("Error read database" ,e);
            return result;
        }
        return result;
    }


}
