package com.codecool.carshare.controller;

import com.codecool.carshare.model.LocationFilter;
import com.codecool.carshare.model.User;
import com.codecool.carshare.service.UserService;
import com.codecool.carshare.service.VehicleService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class VehicleController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LocationFilter locationFilter;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model, HttpSession session,
                        @RequestParam(value = "type", required = false) String filter) {
        model.addAllAttributes(vehicleService.renderVehicles(filter));
        model.addAttribute("user", userService.getSessionUser(session));
        return "index";
    }

    @RequestMapping(value = "/vehicles/{id}", method = RequestMethod.GET)
    public String detailsPage(Model model, @PathVariable("id") String id, HttpSession session) {
        model.addAllAttributes(vehicleService.details(id, session));
        return "details";
    }

    @RequestMapping(value = "/locationData", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject json() {
        return vehicleService.jsonify(vehicleService.getAllLocation());

    }

    @RequestMapping(value = "/locations", method = RequestMethod.GET)
    public String map(Model model, HttpSession session) {
        User user = userService.getSessionUser(session);
        model.addAttribute("user", user);
        return "locations";
    }

    @RequestMapping(value = "/locations/{cityIndex}", method = RequestMethod.POST)
    public String getVehiclesByLocation(@RequestParam("index") String index,
                                        @RequestParam("cityName") String cityName) {
        locationFilter.setVehiclesByLocation(vehicleService.getAllVehiclesByLocation(cityName));
        locationFilter.setCity(cityName);
        return "vehiclesinlocation";
    }

    @RequestMapping(value = "/locations/{cityIndex}", method = RequestMethod.GET)
    public String getVehicles(Model model, HttpSession session) {
        User user = userService.getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("vehicles", locationFilter.getVehiclesByLocation());
        model.addAttribute("location", locationFilter.getCity());
        return "vehiclesinlocation";
    }
}
