package com.codecool.carshare.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationFilter {

    private List<Vehicle> vehiclesByLocation;

    public LocationFilter() {

    }

    public List<Vehicle> getVehiclesByLocation() {
        return vehiclesByLocation;
    }

    public void setVehiclesByLocation(List<Vehicle> vehiclesByLocation) {
        this.vehiclesByLocation = vehiclesByLocation;
    }
}
