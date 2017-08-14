package Statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private static final Logger log = LoggerFactory.getLogger(DataBase.class);

    private Statement statement;
    private Connection connection;
    private String server;

    DataBase(int serverID)
    {
        server = String.valueOf(serverID);
        statement = ConnectDB();
        //TODO исключение при создании БД

        String sql = "CREATE TABLE IF NOT EXISTS '" + server + "' ("
                + " id         INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " user       INTEGER NOT NULL,"
                + " data       INTEGER);";
        try {
            statement.executeUpdate(sql);
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
}
