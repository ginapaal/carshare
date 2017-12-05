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

    public Map<String, Object> details(String id) {
        Map<String, Object> params = new HashMap<>();
        int vehicleId = Integer.valueOf(id);

//        String username = req.session().attribute("user");
//        if (username != null) {
//            User user = dataManager.getUserByName(username);
//            params.put("user", user);
//            if (user != null) {
//                emailAddress = user.getEmail();
//            }
//        }

//        Vehicle resultVehicle = dataManager.getVehicleById(vehicleId);
        params.put("vehicle", vehicleRepository.findVehicleById(vehicleId));

        return params;
    }

    public void reserveVehicle(String id) {
//        Map<String, Object> params = new HashMap<>();
//        int vehicleId = Integer.valueOf(id);
//
//        String username = req.session().attribute("user");
//        if (username != null) {
//            User user = dataManager.getUserByName(username);
//            params.put("user", user);
//            if (user != null) {
//                emailAddress = user.getEmail();
//            }
//        }
//
//        if (username == null) {
//            res.redirect("/login");
//        }
//
//            String resStartDate = req.queryParams("reservation_startdate");
//            String resEndDate = req.queryParams("reservation_enddate");
//            Date startDateRes = new Date();
//            Date endDateRes = new Date();
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                startDateRes = df.parse(resStartDate);
//                endDateRes = df.parse(resEndDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            User user = dataManager.getUserByName(username);
//            Reservation reservation = new Reservation(resultVehicle, user, startDateRes, endDateRes);
//            if (!resultVehicle.setReservation(startDateRes, endDateRes)) {
//                dataManager.persist(reservation);
//                dataManager.update(resultVehicle);
//                reservationMail.sendEmail(emailAddress, username);
//            }
//
//            res.redirect("/");

    }

}
