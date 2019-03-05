package Twitch;

import DiscordBot.Settings.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class twitchAPI {
    private static final Logger log = LoggerFactory.getLogger(twitchAPI.class);

    private static class AuthenticationBody {
        @SerializedName("hub.callback")
        String callback;

        @SerializedName("hub.mode")
        String mode;

        @SerializedName("hub.topic")
        String topic;

        @SerializedName("hub.lease_seconds")
        int lease_seconds;

//        @SerializedName("hub.secret")
//        String secret;
    }

    private static void SubsToChangeStateStreamByID(String userID)
    {
        if(userID.equals(""))
        {
            log.warn("Empty userID");
            return;
        }
        AuthenticationBody auth = new AuthenticationBody();
        auth.mode = "subscribe";
        auth.lease_seconds = 864000;
        auth.callback = "http://" + Settings.body.CALLBACK_ADDR + ":" + Settings.body.CALLBACK_PORT + "/callback";
        auth.topic = "https://api.twitch.tv/helix/streams?user_id=" + userID;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(auth);

        try {
            URL obj = new URL("https://api.twitch.tv/helix/webhooks/hub");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setDoOutput( true );
            con.setRequestMethod("POST");

            con.setRequestProperty( "Content-Type", "application/json");
            con.setRequestProperty("Client-ID",Settings.body.TWITCH_KEY);
            OutputStream os = con.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();
            int responseCode = con.getResponseCode();

            log.info("Sending 'POST' request to URL : " + obj);
            log.info("Response Code : " + responseCode);

            log.debug("Send subs request");

        } catch (IOException e)
        {
            log.warn("Error with request",e);
        }
    }

    static void SubsToChangeStateStreamByName(String name)
    {
        SubsToChangeStateStreamByID(getUserIDbyName(name));
    }

    private static UserInfo getUserIdOrName(String user, String requestUrl)
    {

        StringBuilder response = new StringBuilder();
        try {
            URL obj = new URL(requestUrl + user);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID",Settings.body.TWITCH_KEY);
            int responseCode = con.getResponseCode();

            log.info("Sending 'GET' request to URL : " + obj);
            log.info("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e)
        {
            log.warn("Error with request",e);
        }
        Gson gson = new Gson();
        UserInfoData userInfoData = gson.fromJson(response.toString(),UserInfoData.class);

        if(userInfoData.data.size() > 0)
        {
            return userInfoData.data.get(0);
        }
        return null;
    }
    static String getUserNameById(String id)
    {
        UserInfo userInfo = getUserIdOrName(id,"https://api.twitch.tv/helix/users?id=");
        if(userInfo != null)
        {
            return userInfo.display_name;
        }
        else return "";
    }
    public static String getUserIDbyName(String name)
    {
        UserInfo userInfo = getUserIdOrName(name,"https://api.twitch.tv/helix/users?login=");
        if(userInfo != null)
        {
            return userInfo.id;
        }
        else return "";
    }


}
