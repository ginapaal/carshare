package com.codecool.carshare.model;

import javax.persistence.*;

@NamedQueries({
    @NamedQuery(
            name = "Vehicle.getAll",
            query = "SELECT v FROM Vehicle v"
    ),
    @NamedQuery(
            name = "Vehicle.getByType",
            query = "SELECT v FROM Vehicle v WHERE vehicleType = :type"
    ),

    @NamedQuery(
            name = "Vehicle.getById",
            query = "SELECT v FROM Vehicle v WHERE id = :vehicleId"
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

    private String picture;

    public Vehicle(){

    }

    public Vehicle (String name, int year, int numOfSeats, VehicleType vehicleType, String piclink) {
        this.name = name;
        this.year = year;
        this.numOfSeats = numOfSeats;
        this.vehicleType = vehicleType;
        this.picture = piclink;
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
}
