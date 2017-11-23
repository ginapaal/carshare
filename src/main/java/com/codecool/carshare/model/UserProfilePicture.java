package com.codecool.carshare.model;

import javax.persistence.*;

@Entity
@NamedQuery(name = "getUsersProfPic",
        query = "SELECT upp FROM UserProfilePicture upp WHERE upp.user.id = :user_id")
public class UserProfilePicture {

    @Transient
    private static final String DEFAULT_PICTURE = "/default_pic.jpg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String profilePicture;

    @OneToOne
    private User user;

    public UserProfilePicture() {
        profilePicture = DEFAULT_PICTURE;
    }

    public UserProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }



}
