package com.codecool.carshare.service;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class Mail {

    private String subject;
    private String text;
    private String emailAddress;
    private String username;

    public void setData(MailType type) {
        switch (type) {
            case Welcome:
                subject = "Successfully registered to Carshare!";
                text = "<h1>Welcome to Carshare, " + username + "!</h1>" +
                        "<h3> Thanks for using our awesome application. </h3>" +
                        "<h4> Your registration data: </h4>" +
                        "<p> Username: " + username + "</p>" +
                        "<p>E-mail address: " + emailAddress + "</p>" +
                        "<p> Hope you will find your perfect ride! </p>" +
                        "<p> Cheers: no Idea </p>";
                break;
            case Registration:
                subject = "Successfully reserved a car on Carshare!";
                text = "<h1>" + username + ", you've reserved a car!</h1>" +
                        "<h3> You can discuss about the details with the owner in our message system. </h3>" +
                        "<p> (Which is not implemented yet) </p>" +
                        "<p> Hope you will find your perfect ride! </p>" +
                        "<p> Cheers: no Idea </p>";
        }
    }

    public void sendEmail(String username, String emailAddress, MailType type) {

        this.username = username;
        this.emailAddress = emailAddress;
        setData(type);

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
                        return new PasswordAuthentication("noidea.carshare", "noideacarshare");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noidea.carshare@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailAddress));
            message.setSubject(subject);
            message.setContent(text, "text/html");
            Transport.send(message);
            System.out.println("Message sent");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
