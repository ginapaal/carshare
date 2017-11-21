package com.codecool.carshare.model.email;

public class ReservationMail extends Mail {

    public void setEmailData(String emailAddress, String name) {
        subject = "Successfully reserved a car on Carshare!";
        text = "<h1>" + name + ", you've reserved a car!</h1>" +
                "<h3> You can discuss about the details with the owner in our message system. </h3>" +
                "<p> (Which is not implemented yet) </p>" +
                "<h5> Hope you will find your perfect ride! </h5>" +
                "<h5> Cheers: no Idea </h5>";
    }

}
