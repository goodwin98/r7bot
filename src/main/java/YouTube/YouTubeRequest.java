package YouTube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouTubeRequest {
    private static final Logger log = LoggerFactory.getLogger(YouTubeRequest.class);


    public static YouTubeVideo request(String url) throws Exception {

        //String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCNrzUrkiCUnb8e0nFpgx8Cw&eventType=live&type=video&key=AIzaSyBSDawpBKl-9HvtFeoHDNA083H37DVXJT4";

        StringBuffer response = new StringBuffer();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            // con.setRequestProperty("User-Agent", USER_AGENT);
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
            //System.err.println("ќшибка при get запросе: " + e.getMessage());
            log.warn("Error with request",e);
            throw e;
        }
        //System.out.println("parse : " + parse(response.toString()));

        if (isLive(response.toString()) == false)
                return new YouTubeVideo();
        YouTubeVideo video = parse(response.toString());
        return video;

    }

    private static YouTubeVideo parse(String json) {
        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(json).getAsJsonObject();
        JsonArray items = mainObject.getAsJsonArray("items");
        YouTubeVideo video = new Gson().fromJson(items.get(0),YouTubeVideo.class);
        return video;

    }
    private static boolean isLive(String json)
    {
        try {
            JsonParser parser = new JsonParser();
            JsonObject mainObject = parser.parse(json).getAsJsonObject();
            JsonElement pageInfo = mainObject.get("pageInfo");
            JsonElement totalResults = pageInfo.getAsJsonObject().get("totalResults");
            if (totalResults.getAsInt() != 0) {
                log.info("parse:true");
                return true;
            }
        }
        catch (Exception e){
            log.warn("Error parsing json",e);
            throw e;
        }

        log.info("parse:false");
        return false;
    }
}
