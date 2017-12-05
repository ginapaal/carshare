package com.codecool.carshare.service;

import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Map<String, Object> renderVehicles(String filter) {
        Map<String, Object> params = new HashMap<>();
        VehicleType type = VehicleType.getTypeFromString(filter);
        List results;
        if (type == null) {
            results = vehicleRepository.findAll();
        } else {
            results = vehicleRepository.findVehicleByVehicleType(type);
        }

//        String username = req.session().attribute("user");
//        if (username != null) {
//            User user = dataManager.getUserByName(username);
//            params.put("user", user);
//        }
        params.put("types", Arrays.asList(VehicleType.values()));
        params.put("vehicles", results);
        if (type != null) {
            params.put("selected", type);
        } else {
            params.put("selected", "");
        }

        return params;
    }

    public void saveVehicle(Vehicle entity) {
        vehicleRepository.save(entity);
    }

}
