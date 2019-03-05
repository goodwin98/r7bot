package DiscordBot.Settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

public class Settings {

    private static final Logger log = LoggerFactory.getLogger(Settings.class);


    public static Body body;

    static public boolean load() {

        try(Reader reader = new FileReader("Settings.json")) {
            Gson gson = new Gson();
            body = gson.fromJson(reader,Body.class);
        } catch (Exception e) {
            log.error("Error load settings", e);
            return false;
        }
        return true;
    }

    static public void store(){


        try (Writer writer = new FileWriter("Settings.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(body, writer);
        } catch (Exception e)
        {
            log.error("Error save settings", e);
        }

    }
}
