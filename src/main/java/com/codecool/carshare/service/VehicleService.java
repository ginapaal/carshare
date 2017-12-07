package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.model.email.ReservationMail;
import com.codecool.carshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationMail reservationMail;

    @Autowired
    private User user;

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

    public Map<String, Object> details(String id, HttpSession session) {
        Map<String, Object> params = new HashMap<>();
        int vehicleId = Integer.valueOf(id);

        String username = (String) session.getAttribute("user");
        if (username != null) {
            User user = userService.getUserByName(username);
            params.put("user", user);
        }

//        Vehicle resultVehicle = dataManager.getVehicleById(vehicleId);
        params.put("vehicle", vehicleRepository.findVehicleById(vehicleId));

        return params;
    }

    public Vehicle findVehicleById(int id) {
        return vehicleRepository.findVehicleById(id);
    }


}
