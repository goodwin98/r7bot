package YouTube.MailNotify;

import DiscordBot.Settings.Settings;
import DiscordBot.Settings.YouTubeSubscribeItem;
import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import javax.mail.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subscriber {

    private static final Logger log = LoggerFactory.getLogger(Subscriber.class);
    private static List<SubscribItem> subscribItems = new ArrayList<>();
    private MessageIncomingListener listener;
    private static final String regexp = "(?i)\\b(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?\\b";
    private static final Pattern pattern = Pattern.compile(regexp);
    private Folder inbox;
    private Store store;
    private KeepAliveRunnable runnable;

    public Subscriber()
    {
        Properties properties = System.getProperties();
        properties.put("mail.debug"                 , "false"  );
        properties.put("mail.store.protocol"        , "imaps"  );
        properties.put("mail.imap.ssl.enable"       , "true"   );
        properties.put("mail.imap.port"             , Settings.body.IMAP_Port);
        //properties.put("mail.imaps.usesocketchannels", "true");

        Authenticator auth = new EmailAuthenticator(Settings.body.IMAP_AUTH_EMAIL,
                Settings.body.IMAP_AUTH_PWD);
        Session session = Session.getInstance(properties, auth);
        session.setDebug(false);

        listener = new MessageIncomingListener();
        try {
            store = session.getStore();

            log.info("store and folder connect");

            storeConnect();
            Thread t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        log.info("start idle");

                        ((IMAPFolder) inbox).idle();

                    } catch (StoreClosedException e){
                        log.info("store reconnect");
                        storeConnect();
                    } catch (FolderClosedException  e) {
                        log.info("folder reconnect");
                        folderConnect();
                    } catch (MessagingException e) {
                        log.error("MessagingException",e);
                    } catch (IllegalStateException e){
                        log.error("IllegalStateException",e);
                        storeConnect();
                    } catch (Exception e) {
                        log.error("Exception",e);
                    }
                }
                log.info("thread IdleDoing stopped");
            }, "IdleDoing");
            t.start();

        }
        catch (MessagingException e) {
            log.error("MessagingException",e);
        }
    }
    private void folderConnect()
    {
        try {
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            inbox.addMessageCountListener(listener);

            if (runnable != null){
                runnable.setFolder((IMAPFolder) inbox);
            } else {
                runnable = new KeepAliveRunnable((IMAPFolder) inbox);
                Thread t1 = new Thread(runnable, "NoopPolling");
                t1.start();
            }
        } catch (Exception ex) {
            log.error("Exception", ex);
        }
    }
    private void storeConnect()
    {
        try {
            store.connect(Settings.body.IMAP_Server, Settings.body.IMAP_AUTH_EMAIL, Settings.body.IMAP_AUTH_PWD);

            if (inbox != null && inbox.isOpen())
                inbox.close();
            folderConnect();
        } catch (Exception ex) {
            log.error("Exception",ex);
        }
    }
    private static class KeepAliveRunnable implements Runnable {

        private static final long KEEP_ALIVE_FREQ = 300000; // 5 minutes

        private IMAPFolder folder;

        KeepAliveRunnable(IMAPFolder folder) {
            this.folder = folder;
        }
        void setFolder(IMAPFolder folder){
            this.folder = folder;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(KEEP_ALIVE_FREQ);

                    // Perform a NOOP just to keep alive the connection
                    log.info("Performing a NOOP to keep alive the connection");
                    folder.doCommand(p -> {
                        p.simpleCommand("NOOP", null);
                        return null;
                    });
                } catch (InterruptedException e) {
                    // Ignore, just aborting the thread...
                } catch (MessagingException e) {
                    // Shouldn't really happen...
                    log.warn("Unexpected exception while keeping alive the IDLE connection", e);
                }
            }
            log.info("thread NoopPolling stopped");
        }
    }
    static void parseNewMessage(String message)
    {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find())
        {
            //http://www.youtube.com/watch?v=teXOCKK88-4&feature=em-uploademail
            log.info("link - " + message.substring(matcher.start(),matcher.end()));
            Map<String,String> param = queryToMap(message.substring(matcher.start(),matcher.end()));
            if(!param.containsKey("feature") || !param.containsKey("v"))
                continue;
            if(!param.get("feature").equals("em-lbcastemail") && !param.get("feature").equals("em-uploademail"))
                continue;
            try {

                YouTubeVideo video = YouTubeAPI.getVideoByID(param.get("v"));
                for(SubscribItem item : subscribItems) {
                    if(!item.isYtChannelContains(video.snippet.channelId))
                        continue;
                    if (param.get("feature").equals("em-lbcastemail")) {
                        log.info("live channel " + video.snippet.channelTitle);
                        item.getChannel().sendMessage("@everyone, " + video.snippet.channelTitle + " начал стримить!",MessageBuilder.chanIsLive(video));
                    } else {
                        log.info("new video on channel " + video.snippet.channelTitle);
                        item.getChannel().sendMessage("@everyone, новое видео на канале " + video.snippet.channelTitle,MessageBuilder.newVideoOnChan(video));
                    }
                }
            } catch (Exception e)
            {
                log.error("error with parsing ",e);
            }

        }
    }

    public void addGuild(IGuild guild)
    {
        for(SubscribItem subscribItem: subscribItems)
        {
            if(subscribItem.getGuild().equals(guild))
            {
                return;
            }
        }
        for(YouTubeSubscribeItem settingsItem: Settings.body.youTubeSubscribeItemList)
        {
            if(settingsItem.guild == guild.getLongID() && guild.getChannelByID(settingsItem.channel) != null)
            {
                SubscribItem item = new SubscribItem(guild,guild.getChannelByID(settingsItem.channel), settingsItem.ytChans);
                subscribItems.add(item);
                log.info("YT Subscribe settings for guild " + guild.getName() + " has been added");
            }
        }
    }

    private static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<>();
        query = query.substring(1 + query.indexOf("?"));
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
