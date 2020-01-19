package com.dmgburg.bookshareserver;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Component
public class MailingService {
    private final Session session;
    private final String baseURL;
    public MailingService(@Value("${dmgburg.mail.baseUrl}") String baseUrl,
                            @Value("${dmgburg.mail.username}") String username,
                            @Value("${dmgburg.mail.password}") String password) {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        prop.setProperty("mail.smtp.host", "smtp.gmail.com");
        prop.setProperty("mail.smtp.port", "587");
        this.session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        this.baseURL = baseUrl;
    }

    public void sendMessage(String msg, String to) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("dmgburg@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse("transformator00@mail.ru"));
            message.setSubject("Mail Subject");

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void sendBookReqest(String holder, String requestor, String name, Long id) throws IOException {
        URL url = Resources.getResource("foo.txt");
        String text = Resources.toString(url, Charsets.UTF_8);
        text = text.replace("${requestor}", requestor);
        text = text.replace("${bookName}", name);
        text = text.replace("${baseURL}", baseURL);
        text = text.replace("${requestId}", baseURL);
        sendMessage(text, holder);
    }
}
