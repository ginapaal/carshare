package com.codecool.carshare.controller;

import com.codecool.carshare.service.ReservationService;
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
    private ReservationService reservationService;

    @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.POST)
    public String reserveVehicle(HttpSession session, Model model,
                                 @PathVariable("id") String vehicleId,
                                 @RequestParam("reservation_startdate") String resStartString,
                                 @RequestParam("reservation_enddate") String resEndString) {

        if (reservationService.reserveVehicle(model, session, vehicleId, resStartString, resEndString)) {
            return "redirect:/vehicles/" + vehicleId + "/reservation";
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
}
