package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.service.UserService;
import com.codecool.carshare.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Controller
public class RouteController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    private User user = new User();
//    get("/register", pageController::register);
//    post("/register", pageController::register);
//    get("/login", pageController::login);
//    post("/login", pageController::login);
//    get("/logout", pageController::logout);
//    get("/upload", pageController::uploadVehicle);
//    post("/upload", pageController::uploadVehicle);
//    get("/user/:id", pageController::profile);
//    post("/:id/upload-profile-pic", pageController::profile);
//    get("/vehicles/:id", pageController::details);
//    post("/vehicles/:id", pageController::details);
//    get("/", pageController::renderVehicles);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model,
                        @RequestParam(value = "type", required = false) String filter) {
        model.addAllAttributes(vehicleService.renderVehicles(filter));
        return "index";
    }

    @RequestMapping(value = "/vehicles/{id}", method = RequestMethod.GET)
    public String detailsPage(Model model, @PathVariable("id") String id) {
        model.addAllAttributes(vehicleService.details(id));
        return "details";
    }

    @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.POST)
    public String reservation (Model model, @PathVariable("id") String id, @ModelAttribute("user") User user) {
//        model.addAllAttributes(vehicleService.reserveVehicle(id));
        return "details";
    }

    @RequestMapping(value = "/register", method=RequestMethod.GET)
    public String registerPage(Model model) {
        return "register";
    }

    @RequestMapping(value = "/register", method= RequestMethod.POST)
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirm-password") String confirmPassword)
    throws NoSuchAlgorithmException, InvalidKeySpecException{
        model.addAttribute(userService.registration(username, email, password, confirmPassword));
        return "redirect:/";
    }



}
