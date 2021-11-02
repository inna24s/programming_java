package lab.server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


class JavaMail {
    static final String ENCODING = "UTF-8";

    public static void registration(String email, String reg_token){
        String subject = "Confirm registration";
        String content = "Random Password: " + reg_token +". Don't show it to anyone!";
        String smtpHost="smtp.gmail.com";
        String from= "inna-voronkova0@mail.ru";
   //String login="inna-voronkova0@mail.ru";
    //   String password="byecz2000";
        String login="lesti0505@gmail.com";
        String password="yanerobot";
        String smtpPort="25";
        try {
            sendSimpleMessage(login, password, from, email, content, subject, smtpPort, smtpHost);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error whilst sending email");
        }
    }



    public static void sendSimpleMessage(String login, String password, String from, String to, String content, String subject, String smtpPort, String smtpHost)
            throws MessagingException {
        Authenticator auth = new Auth(login, password);

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.mime.charset", ENCODING);
        Session session = Session.getDefaultInstance(props, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(content);
        Transport.send(msg);
    }
}