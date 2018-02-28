package YouTube.MailNotify;

import com.sun.mail.imap.IdleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import java.io.IOException;

public class MessageIncomingListener extends MessageCountAdapter {

    private static final Logger log = LoggerFactory.getLogger(MessageIncomingListener.class);
    IdleManager idleManager;

    MessageIncomingListener()
    {

    }

    @Override
    public void messagesAdded(MessageCountEvent ev){
        try {
            Folder folder = (Folder) ev.getSource();
            Message[] msgs = ev.getMessages();

            log.info("Folder: " + folder + " got " + msgs.length + " new messages");

            for (Message msg : msgs) {
                InternetAddress address = (InternetAddress) msg.getFrom()[0];
                if (!address.getAddress().equals("goodwinnt@gmail.com") && !address.getAddress().equals("noreply@youtube.com"))
                    continue;
                String messageText = "No body message";
                log.debug("ContentType - " + msg.getContentType());
                if (msg.getContentType().toLowerCase().startsWith("multipart/ALTERNATIVE".toLowerCase())) {
                    Multipart mp = (Multipart) msg.getContent();

                    for (int i = 0; i < mp.getCount(); i++) {
                        if (mp.getBodyPart(i).getContentType().toLowerCase().startsWith("text/plain")) {
                            BodyPart bp = mp.getBodyPart(i);
                            messageText = (String) bp.getContent();
                        }
                    }
                } else {
                    messageText = (String) msg.getContent();
                }
                log.debug("сообщение : '" +
                        messageText + "'");
                Subscriber.parseNewMessage(messageText);
            }
            log.debug("watch "+ folder.toString());
            //idleManager.watch(folder); // keep watching for new messages
        } catch (MessagingException e) {
            log.error("MessagingException" ,e);
        } catch (IOException e) {
            log.error("IOException",e);
        }
    }


}
