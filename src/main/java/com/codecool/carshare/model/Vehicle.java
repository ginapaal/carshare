package com.codecool.carshare.model;

import javax.persistence.*;

@NamedQueries({
    @NamedQuery(
            name = "Vehicle.getAll",
            query = "SELECT v FROM Vehicle v"
    ),
    @NamedQuery(
            name = "Vehicle.getByType",
            query = "SELECT v FROM Vehicle v WHERE vehicleType = :type" //TODO
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

    public Vehicle(){

    }

    public Vehicle (String name, int year, int numOfSeats, VehicleType vehicleType) {
        this.name = name;
        this.year = year;
        this.numOfSeats = numOfSeats;
        this.vehicleType = vehicleType;
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
}
