package com.codecool.carshare.controller;

import com.codecool.carshare.service.ReservationService;
import com.codecool.carshare.service.UserService;
import com.codecool.carshare.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class ReservationController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(value ="/vehicles/{id}/reservation", method =RequestMethod.GET)
    public String reserveVehicle() {
        return "redirect:/";
    }

    @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.POST)
    public String reserveVehicle(HttpSession session, Model model,
                                 @PathVariable("id") String vehicleId,
                                 @RequestParam("reservation_startdate") String resStartString,
                                 @RequestParam("reservation_enddate") String resEndString) {

        if (reservationService.reserveVehicle(model, session, vehicleId, resStartString, resEndString)) {
            return "redirect:/vehicles/" + vehicleId + "/billing";
        } else {
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

    @RequestMapping(value = "/billingData", method = RequestMethod.POST)
    public String makeReservation(HttpSession session) {
        reservationService.makeReservation(session);
        return "redirect:/";
    }

    @RequestMapping(value = "/vehicles/{id}/billing", method = RequestMethod.GET)
    public String billingInfoPage(HttpSession session, Model model,
                                  @PathVariable("id") String vehicleId) {
        if (session.getAttribute("reservation") != null && session.getAttribute("user") != null) {
            model.addAttribute("user", userService.getSessionUser(session));
            model.addAttribute("vehicle", vehicleService.findVehicleById(Integer.valueOf(vehicleId)));
            model.addAttribute("reservation", session.getAttribute("reservation"));
            return "billing";
        } else {
            return "redirect:/vehicles/" + vehicleId;
        }
    }
}
