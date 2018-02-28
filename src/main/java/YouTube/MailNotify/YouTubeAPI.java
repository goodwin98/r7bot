package YouTube.MailNotify;

import DiscordBot.Settings.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class YouTubeAPI {

    private static final Logger log = LoggerFactory.getLogger(YouTubeAPI.class);

    private static String YouTubeRequest(String url) throws IOException{


        StringBuilder response = new StringBuilder();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            con.setRequestProperty("Accept-Charset", "UTF-8");

            int responseCode = con.getResponseCode();
            log.info("Sending 'GET' request to URL : " + url);
            log.info("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        catch(IOException e) {
            log.warn("Error with request",e);
            throw e;
        }
        return response.toString();
    }
    static YouTubeVideo getVideoByID(String id) throws Exception{
        String requestUrl = "https://www.googleapis.com/youtube/v3/videos?id=" + id + "&part=snippet&key=" + Settings.body.YOUTUBE_KEY;
        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(YouTubeRequest(requestUrl)).getAsJsonObject();
        JsonArray items = mainObject.getAsJsonArray("items");
        return new Gson().fromJson(items.get(0),YouTubeVideo.class);
    }
}
