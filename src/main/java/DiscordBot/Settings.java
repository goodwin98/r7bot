package DiscordBot;

import Statistic.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final Logger log = LoggerFactory.getLogger(Settings.class);


    static String BOT_TOKEN;

    static String BOT_PREFIX;

    static public boolean load() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("config.prop")) {

            properties.load(fis);
            BOT_TOKEN = properties.getProperty("BOT_TOKEN");
            BOT_PREFIX = properties.getProperty("BOT_PREFIX");


        } catch (IOException e){
            log.error("Settings file not load", e);
            return false;
        }

        return true;
    }

    static public void store(){

        Properties properties = new Properties();

        try(FileOutputStream fos = new FileOutputStream("config.prop")) {

            properties.setProperty("BOT_TOKEN", BOT_TOKEN);
            properties.setProperty("BOT_PREFIX", BOT_PREFIX);

            properties.store(fos, "comment");
        }
        catch (IOException e)
        {

        }

    }
}
