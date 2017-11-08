package com.codecool.carshare.model;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(
                name = "User.getPasswordHash",
                query = "SELECT passwordHash FROM User WHERE name = :name"
        )
})

@Entity
@Table(name="Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    public User(){}

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

}
