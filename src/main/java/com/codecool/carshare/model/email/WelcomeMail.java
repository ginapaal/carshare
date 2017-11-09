package com.codecool.carshare.model.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class WelcomeMail {
    public WelcomeMail(){}

    public void sendEmail(String emailAddress, String name) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("noidea.carshare","noideacarshare");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noidea.carshare@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailAddress));
            message.setSubject("Successfully registered to Carshare!");
            String text = "<h1>Welcome to Carshare, " + name + "!</h1>";
            text += "<h3> Thanks for using our awesome application. </h3>";
            text += "<h4> Your registration data: </h4>";
            text += "<p> Username: " + name + "</p>";
            text += "<p>E-mail address: " + emailAddress + "</p>";
            text += "<h5> Hope you will find your perfect ride! </h5>";
            text += "<h5> Cheers: no Idea </h5>";
            message.setContent(text, "text/html");
            Transport.send(message);
            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
