package com.codecool.carshare.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationFilter {

    private String city;
    private List<Vehicle> vehiclesByLocation;

    public LocationFilter() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Vehicle> getVehiclesByLocation() {
        return vehiclesByLocation;
    }

    public void setVehiclesByLocation(List<Vehicle> vehiclesByLocation) {
        this.vehiclesByLocation = vehiclesByLocation;
    }
}
