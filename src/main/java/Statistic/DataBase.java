package Statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DataBase {
    private static final Logger log = LoggerFactory.getLogger(DataBase.class);

    Statement statement;
    String server;

    void DataBase (int serverID, List<String> chans)
    {
        server = String.valueOf(serverID);
        statement = ConnectDB();
        //TODO исключение при создании БД

        String sql = "CREATE TABLE IF NOT EXISTS " + server + " ("
                + " id         INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " user       INTEGER NOT NULL,"
                + " data       INTEGER";
        for(String chan: chans)
        {
            sql += ", '" + chan + "' INTEGER";
        }
        sql += ");";


    }

    private Statement ConnectDB()
    {
        //Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        Statement statement = null;

        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:statistics.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.


        }
        catch (SQLException e)
        {
            log.error("Error in create database",e);
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                log.error("connection close failed.",e);
            }
        }
        return statement;

    }
}
