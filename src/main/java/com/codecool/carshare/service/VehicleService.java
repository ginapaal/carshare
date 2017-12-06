package com.codecool.carshare.service;

import com.codecool.carshare.model.Reservation;
import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.model.email.ReservationMail;
import com.codecool.carshare.repository.UserRepository;
import com.codecool.carshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

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
            User user = userRepository.getUserByName(username);
            params.put("user", user);
        }

        params.put("vehicle", vehicleRepository.findVehicleById(vehicleId));

        return params;
    }

    public Map<String, Object> reserveVehicle(String id, String resStartDate, String resEndDate,
                                              HttpSession session) {
        Map<String, Object> params = new HashMap<>();
        int vehicleId = Integer.valueOf(id);
        Vehicle vehicle = vehicleRepository.findVehicleById(vehicleId);

        Date startDateRes = new Date();
        Date endDateRes = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDateRes = df.parse(resStartDate);
            endDateRes = df.parse(resEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String username = (String) session.getAttribute("user");
        User user = (User) userRepository.getUserByName(username);
        String emailAddress = user.getEmail();
        Reservation reservation = new Reservation(vehicle, user, startDateRes, endDateRes);
        if (!vehicle.setReservation(startDateRes, endDateRes)) {
            reservationMail.sendEmail(emailAddress, username);
        }

        return params;

    }

    public Map<String, Object> uploadVehicle(String name,
                                             String year,
                                             String seats,
                                             String type,
                                             String piclink,
                                             String startDate,
                                             String endDate,
                                             String location,
                                             HttpSession session) {
        Map<String, Object> params = new HashMap<>();
        String username = (String) session.getAttribute("user");
        if (username != null) {
            User user = userRepository.getUserByName(username);
            params.put("user", user);
        }

        VehicleType vehicleType = VehicleType.getTypeFromString(type);

        int yearInt = Integer.parseInt(year);
        int numOfSeats = Integer.parseInt(seats);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDateF = df.parse(startDate);
            Date endDateF = df.parse(endDate);
            Vehicle vehicle = new Vehicle(name, yearInt, numOfSeats, vehicleType, piclink, startDateF, endDateF, location);
            // sets owner to uploaded car
            User owner = userRepository.getUserByName(username);
            vehicle.setOwner(owner);
            owner.addVehicle(vehicle);
            vehicle.setAvailability();
            saveVehicle(vehicle);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return params;
    }

}
