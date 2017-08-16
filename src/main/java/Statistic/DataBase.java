package Statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBase {
    private static final Logger log = LoggerFactory.getLogger(DataBase.class);

    private Statement statement;
    private Connection connection;
    private String server;

    static final private String sqlCreateTable = "CREATE TABLE IF NOT EXISTS '%s' ("
            + " id         INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " user       STRING NOT NULL,"
            + " data       INTEGER);";

    static final private String sqlGetTableInfo = "PRAGMA table_info('%s');";
    static final private String sqlAddColumn = "ALTER TABLE '%s' ADD COLUMN '%s' INTEGER;";
    static final private String sqlGetRowByUserAndDate = "SELECT * from '%s' WHERE user = '%s' AND data = %s;";

    DataBase(int serverID)
    {
        server = String.valueOf(serverID);
        statement = ConnectDB();
        //TODO исключение при создании БД

        try {
            statement.executeUpdate(String.format(sqlCreateTable,server));
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
    void saveUser(User user){

        List<String> tableColumns = new ArrayList<>();
        List<String> needCulumsToAdd = new ArrayList<>();
        try {
            ResultSet info = statement.executeQuery(String.format(sqlGetTableInfo, server));

            while (info.next()) {
                tableColumns.add(info.getString("name"));
            }
            Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));
            LocalDate ld = LocalDate.now(clock);
            String nowData = DateTimeFormatter.ofPattern("yyyyMMdd").format(ld);

            ResultSet row = statement.executeQuery(String.format(sqlGetRowByUserAndDate,server,user.getUser().getStringID(),nowData));

            //TODO сделать все это добро на StringBuilder
            if(row.next())
            {
                String sql = "UPDATE '"+server+ "' SET data = " + nowData;
                for (Map.Entry<IChannel, Long> entry : user.getAllTime().entrySet()) {

                    int old;
                    if (!tableColumns.contains(entry.getKey().getStringID())) {
                        needCulumsToAdd.add(entry.getKey().getStringID());
                        old = 0;
                    } else {
                        old = row.getInt(entry.getKey().getStringID());
                    }
                    sql += ", '" + entry.getKey().getStringID() + "' = " + String.valueOf(old + entry.getValue()/1000);
                }
                sql += " WHERE user = " + user.getUser().getStringID() + " AND data = " + nowData + ";";
                addColumsToDB(needCulumsToAdd);
                statement.executeUpdate(sql);
            }else {
                String sql1 = "INSERT OR REPLACE INTO '" + server + "' (user, data ";
                String sql2 = "VALUES (" + user.getUser().getStringID() + ", " + nowData;
                for (Map.Entry<IChannel, Long> entry : user.getAllTime().entrySet()) {
                    if (!tableColumns.contains(entry.getKey().getStringID())) {
                        needCulumsToAdd.add(entry.getKey().getStringID());
                    }
                    sql1 += ", '" + entry.getKey().getStringID() + "' ";
                    sql2 += ", " + entry.getValue()/1000;

                }
                String sql = sql1 + ") " + sql2 + ");";
                addColumsToDB(needCulumsToAdd);
                statement.executeUpdate(sql);
            }


        } catch (SQLException e) {
            log.error("User not saved to DB" ,e);
        }

    }
    private void addColumsToDB(List<String> colums) throws SQLException
    {

        for( String colum : colums)
        {
            statement.executeUpdate(String.format(sqlAddColumn,server,colum));
        }

    }
}
