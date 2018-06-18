package Statistic.Presence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class DataBase {
    private static final Logger log = LoggerFactory.getLogger(Statistic.Presence.DataBase.class);

    private Statement statement;
    private static Connection connection = null;


    DataBase()
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
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");

            if(connection == null) {
                connection = DriverManager.getConnection("jdbc:sqlite:statistics_pres.db");
            }
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
        String sqlCreateUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "    id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    UserID STRING  UNIQUE NOT NULL," +
                "    LastOnLineData INTEGER" +
                ");";
        String sqlCreateGames = "CREATE TABLE IF NOT EXISTS games (" +
                "    id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    Game   STRING  UNIQUE NOT NULL" +
                ");";
        String sqlCreateStatGames = "CREATE TABLE IF NOT EXISTS statGames (" +
                "    id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    User   INTEGER NOT NULL," +
                "    Date   INTEGER NOT NULL," +
                "    Game   INTEGER NOT NULL," +
                "    Seconds INTEGER" +
                ");";

        statement.executeUpdate(sqlCreateUsers);
        statement.executeUpdate(sqlCreateGames);
        statement.executeUpdate(sqlCreateStatGames);
    }

    private int getGameID(String game)  throws SQLException
    {
        PreparedStatement stmt;
        String sqlSelect = "SELECT id FROM games WHERE Game = ?;";
        String sqlInsert = "INSERT INTO games (Game) VALUES (?);";

        stmt = connection.prepareStatement(sqlSelect);
        stmt.setString(1, game);
        ResultSet row = stmt.executeQuery();
        if(!row.next())
        {
            stmt = connection.prepareStatement(sqlInsert);
            stmt.setString(1, game);
            stmt.executeUpdate();

            stmt = connection.prepareStatement(sqlSelect);
            stmt.setString(1, game);
            row = stmt.executeQuery();
        }
        return row.getInt("id");
    }

    private int getUserID(long user) throws SQLException
    {
        String sqlSelect = "SELECT id FROM users WHERE UserID = '"+ user +"';";
        String sqlInsert = "INSERT INTO users (UserID) VALUES ('"+ user +"');";

        ResultSet row = statement.executeQuery(sqlSelect);
        if(!row.next())
        {
            statement.executeUpdate(sqlInsert);
            row = statement.executeQuery(sqlSelect);
        }
        return row.getInt("id");
    }

    void savePresenceStat(User user)
    {
        try {

            connection.setAutoCommit(false);
            try {
                singleSavePresence(user);
                connection.commit();
            }  catch (SQLException e) {
                log.error("Error save to dataBase" ,e);
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e)
        {
            log.error("Error save to dataBase" ,e);
        }
    }


    private void singleSavePresence( User user) throws SQLException
    {

            int gameID = getGameID(user.getCurrentGame());
            int userID = getUserID(user.getUser().getLongID());

            int nowDate = formatDate(0);
            String sqlUserUpdate = "UPDATE users SET LastOnLineData = "+ nowDate +" WHERE id = "+ userID +";";
            statement.executeUpdate(sqlUserUpdate);

            String sqlStatSelect = "SELECT id, Seconds FROM statGames " +
                    "WHERE User = "+ userID +" AND Date = "+ nowDate +" AND Game = "+ gameID + ";";
            ResultSet row = statement.executeQuery(sqlStatSelect);

            if(!row.next())
            {
                String sqlStatInsert = "INSERT INTO statGames (User, Date, Game, Seconds)" +
                        " VALUES ("+ userID +", " + nowDate +", "+ gameID+", "+ (user.getTime()/1000) + ");";
                statement.executeUpdate(sqlStatInsert);
            } else {
                int oldSeconds = row.getInt("Seconds");
                int oldId = row.getInt("id");
                String sqlStatUpdate = "UPDATE statGames SET Seconds = "+ (oldSeconds + user.getTime()/1000) +" WHERE id = "+ oldId +";";
                statement.executeUpdate(sqlStatUpdate);
            }

    }
    void updateUserLastDate(long user)
    {
        try {
            int userID = getUserID(user);
            int nowDate = formatDate(0);
            String sqlUserUpdate = "UPDATE users SET LastOnLineData = " + nowDate + " WHERE id = " + userID + ";";
            statement.executeUpdate(sqlUserUpdate);
        } catch (SQLException e) {
            log.error("Error save to dataBase" ,e);
        }
    }
    void updateUsersLastDate(List<Long> users)
    {
        try {
            connection.setAutoCommit(false);
            for(Long user : users)
            {
                updateUserLastDate(user);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error save to dataBase" ,e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e)
            {
                log.error("Error save to dataBase" ,e);
            }
        }
    }
    int getLastDateOnline(long user)
    {
        int date = 0;
        String sqlSelect = "select LastOnLineData from users where UserID = ?;";
        try {
            PreparedStatement smt = connection.prepareStatement(sqlSelect);
            smt.setString(1, Long.toString(user));
            ResultSet row = smt.executeQuery();
            if(row.next())
            {
                date = row.getInt("LastOnLineData");
            }

        } catch (SQLException e)
        {
            log.error("Error read get last date from dataBase" ,e);
        }

        return date;
    }
    List<String> getTopGames()
    {
        String sqlSelect = "select games.Game from statGames" +
                " join games on statGames.Game = games.id join users on User = users.id " +
                " group by statGames.Game order by sum(Seconds) desc LIMIT 20;";
        List<String> result = new ArrayList<>();
        try {
            ResultSet row = statement.executeQuery(sqlSelect);

            while (row.next())
            {
                result.add(row.getString("Game"));
            }
            return result;
        } catch (SQLException e) {
            log.error("Error read getTopGames from dataBase" ,e);
        }
        return result;
    }

    private static int formatDate(int offsetDays)
    {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        zdt = zdt.minusDays(offsetDays);
        return Integer.parseInt(DateTimeFormatter.ofPattern("yyyyMMdd").format(zdt));
    }


}
