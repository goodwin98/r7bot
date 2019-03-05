package Twitch;

import DiscordBot.Settings.Settings;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebHookServer {

    private static final Logger log = LoggerFactory.getLogger(WebHookServer.class);
    private Helper helper;

    public WebHookServer()
    {
        helper = new Helper();
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(Settings.body.CALLBACK_PORT), 0);
            server.createContext("/callback", new MyHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            //twitchAPI.SubsToChangeStateStreamByName("yxo_progressive");
        } catch (IOException e)
        {
            log.error("Error start http server",e);
        }
    }

    public Helper getHelper()
    {
        return helper;
    }

    class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            log.debug("new connect");
            log.debug("Method : " + t.getRequestMethod());
            for (Map.Entry<String,List<String>> entry: t.getRequestHeaders().entrySet())
            {
                log.debug(entry.getKey() + " - " + entry.getValue());
            }
            if(t.getRequestMethod().equals("GET")) {
                if(t.getRequestURI().getQuery() != null) {
                    Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        log.debug(entry.getKey() + " - " + entry.getValue());
                    }
                    if (params.containsKey("hub.challenge")) {
                        t.sendResponseHeaders(200, params.get("hub.challenge").length());
                        OutputStream os = t.getResponseBody();
                        os.write(params.get("hub.challenge").getBytes());
                        os.close();
                    } else {
                        t.sendResponseHeaders(200, -1);
                    }
                } else {
                    log.debug("empty query");
                    t.sendResponseHeaders(200, -1);
                }
            }

            //log.debug(IOUtils.toString(t.getRequestBody(), StandardCharsets.UTF_8));

            helper.NewLiveStreamEvent(t.getRequestBody());

            t.sendResponseHeaders(200,-1);


        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
