package com.codecool.carshare.model.email;

public class WelcomeMail extends Mail {

    public void setEmailData(String emailAddress, String name) {
        subject = "Successfully registered to Carshare!";
        text = "<h1>Welcome to Carshare, " + name + "!</h1>" +
                "<h3> Thanks for using our awesome application. </h3>" +
                "<h4> Your registration data: </h4>" +
                "<p> Username: " + name + "</p>" +
                "<p>E-mail address: " + emailAddress + "</p>" +
                "<h5> Hope you will find your perfect ride! </h5>" +
                "<h5> Cheers: no Idea </h5>";
    }

}