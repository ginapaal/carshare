package com.codecool.carshare.model;

import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.persistence.*;
import java.util.Date;

@NamedQueries({
        @NamedQuery(
                name = "Vehicle.getAll",
                query = "SELECT v FROM Vehicle v"
        ),
        @NamedQuery(
                name = "Vehicle.getByType",
                query = "SELECT v FROM Vehicle v WHERE vehicleType = :type" //TODO
        ),
        @NamedQuery(
                name = "Vehicle.getByOwner",
                query = "SELECT v FROM Vehicle v WHERE v.owner = :owner_id"
        )
})
@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int year;
    private int numOfSeats;

    @Enumerated
    private VehicleType vehicleType;

    @ManyToOne
    private User owner;


    private Date startDate;
    private Date endDate;

    private String picture;

    @Transient
    private boolean isAvailable;

    public Vehicle() {

    }

    public Vehicle(String name, int year, int numOfSeats, VehicleType vehicleType, String piclink, Date startDate, Date endDate) {
        this.name = name;
        this.year = year;
        this.numOfSeats = numOfSeats;
        this.vehicleType = vehicleType;
        this.picture = piclink;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getYear() {
        return year;
    }

    public int getNumOfSeats() {
        return numOfSeats;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void isAvailable(Date startDate, Date endDate){

    }
}
