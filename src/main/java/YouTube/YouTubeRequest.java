package YouTube;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class YouTubeRequest {
    private static final Logger log = LoggerFactory.getLogger(YouTubeRequest.class);


    static YouTubeVideo request(String url) throws Exception {


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

        if (isLive(response.toString()) == false)
                return new YouTubeVideo();
        return parse(response.toString());

    }

    private static YouTubeVideo parse(String json) {
        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(json).getAsJsonObject();
        JsonArray items = mainObject.getAsJsonArray("items");
        return new Gson().fromJson(items.get(0),YouTubeVideo.class);

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
