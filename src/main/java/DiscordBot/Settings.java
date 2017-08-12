package DiscordBot;

import Statistic.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


class Settings {
    private static final Logger log = LoggerFactory.getLogger(Settings.class);


    static String BOT_TOKEN;

    static String BOT_PREFIX;

    static boolean load() {
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

    static void store(){



    }
}
