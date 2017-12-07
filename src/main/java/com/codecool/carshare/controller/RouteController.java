package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.service.ReservationService;
import com.codecool.carshare.service.UserService;
import com.codecool.carshare.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Controller
public class RouteController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ReservationService reservationService;

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

   /* @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.POST)
    public String reservation (Model model,
                               @PathVariable("id") String id,
                               @RequestParam("reservation_startdate") String resStartDate,
                               @RequestParam("reservation_enddate") String resEndDate) {

        model.addAllAttributes(vehicleService.reserveVehicle(id, resStartDate, resEndDate));
        return "details";
    }*/

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPage(Model model,
                            HttpSession session,
                            @RequestParam("username") String username,
                            @RequestParam("password") String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        model.addAllAttributes(userService.login(username, password, session));
        return "redirect:/";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(Model model, HttpSession session,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("email") String email,
                               @RequestParam("confirm-password") String confirmPassword)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (userService.register(username, password, confirmPassword, email, model)) {
            session.setAttribute("user", username);
            return "redirect:/";
        }
        else {
            return "/register";
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String renderProfilePage(Model model, HttpSession session) {
        model.addAttribute("user", userService.getSessionUser(session));
        model.addAttribute("uploadpage", true);
        return "userProfile";
    }

    @RequestMapping(value = "/{id}/upload-profile-pic", method = RequestMethod.POST)
    public String uploadProfilePicture(HttpSession session,
                                       @PathVariable("id") String id,
                                       @RequestParam("profilePicture") String profilePicture) {
        userService.uploadProfilePicture(userService.getSessionUser(session), profilePicture);
        return "redirect:/user/" + id;
    }

    @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.POST)
    public String reserveVehicle(HttpSession session, Model model,
                                 @PathVariable("id") String vehicleId,
                                 @RequestParam("reservation_startdate") String resStartString,
                                 @RequestParam("reservation_enddate") String resEndString) {

        if (reservationService.reserveVehicle(model, session, vehicleId, resStartString, resEndString)) {
            return "redirect:/vehicles/" + vehicleId + "/reservation";
        }
        else {
            if (model.containsAttribute("error")) {
                String error = (String) model.asMap().get("error");
                if (error.equals("not_logged_in")) {
                    return "redirect:/login";
                }
                if (error.equals("invalid_date")) {
                    return "redirect:/vehicles/" + vehicleId;
                }
            }

            return "redirect:/";
        }
    }

    @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.GET)
    public String billingInfoPage(HttpSession session, Model model,
                                  @PathVariable("id") String vehicleId) {
        if (session.getAttribute("reservation") != null) {
            model.addAttribute("user", userService.getSessionUser(session));
            model.addAttribute("vehicle", vehicleService.findVehicleById(Integer.valueOf(vehicleId)));
            model.addAttribute("reservation", session.getAttribute("reservation"));
            return "billing";
        }
        else {
            return "redirect:/vehicles/" + vehicleId;
        }
    }

    @RequestMapping(value = "/reserve/{vehicleId}", method = RequestMethod.POST)
    public String makeReservation(HttpSession session,
                                  @PathVariable("vehicleId") String vehicleId) {

        reservationService.makeReservation(vehicleId, session);

        return "redirect:/";
    }
}
