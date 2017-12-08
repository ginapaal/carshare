package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.repository.UserRepository;
import com.codecool.carshare.repository.VehicleRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    private UserService userService;

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

    public Map<String, Object> details(String id, HttpSession session) {
        Map<String, Object> params = new HashMap<>();
        int vehicleId = Integer.valueOf(id);

        String username = (String) session.getAttribute("user");
        if (username != null) {
            User user = userService.getUserByName(username);
            params.put("user", user);
        }

        params.put("vehicle", vehicleRepository.findVehicleById(vehicleId));

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
        User owner;
        if (username != null) {
            owner = userRepository.getUserByName(username);
            params.put("user", owner);
        }
        else {
            params.put("error", "not_logged_in");
            return params;
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
            vehicle.setOwner(owner);
            owner.addVehicle(vehicle);
            vehicle.setAvailability();
            saveVehicle(vehicle);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return params;
    }


    public void saveVehicle(Vehicle entity) {
        vehicleRepository.save(entity);
    }

    public Vehicle findVehicleById(int id) {
        return vehicleRepository.findVehicleById(id);
    }

    public List<String> getAllLocation() {

        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<String> vehicAddress = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            vehicAddress.add(vehicle.getLocation());
        }
        return vehicAddress;
    }

    public JSONObject jsonify(List<String> vehicleAddress) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < vehicleAddress.size(); i++) {
            JSONObject locationToAdd = new JSONObject();
            locationToAdd.put("location", vehicleAddress.get(i));
            locationToAdd.put("index", i);
            locationToAdd.put("url", "/locations/"+i+"");
            jsonArray.add(locationToAdd);
        }

        jsonObject.put("locationlist", jsonArray);
        return jsonObject;
    }

    public List<Vehicle> getAllVehiclesByLocation(String location) {
        return vehicleRepository.findAllByLocation(location);
    }

}
