package com.codecool.carshare.repository;

import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>{

    List<Vehicle> findVehicleByVehicleType(VehicleType vehicleType);
    Vehicle findVehicleById(Integer vehicleId);
    List<Vehicle> findAllByLocation(String location);

}
