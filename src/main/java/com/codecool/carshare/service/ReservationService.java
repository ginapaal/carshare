package com.codecool.carshare.service;


import com.codecool.carshare.model.Reservation;
import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mail mail;

    public boolean reserveVehicle(Model model, HttpSession session, String vehicleIdString, String resStartDate, String resEndDate) {
        int vehicleId = Integer.valueOf(vehicleIdString);
        Vehicle vehicle = vehicleService.findVehicleById(vehicleId);

        String userName = (String) session.getAttribute("user");
        if (userName == null) {
            model.addAttribute("error", "not_logged_in");
            return false;
        }

        User user = userService.getUserByName(userName);

        Date startDateRes;
        Date endDateRes;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDateRes = df.parse(resStartDate);
            endDateRes = df.parse(resEndDate);
            Reservation reservation = new Reservation(vehicle, user, startDateRes, endDateRes);
            model.addAttribute("reservation", reservation);
            session.setAttribute("reservation", reservation);
            return vehicle.checkReservationDate(startDateRes, endDateRes);
        } catch (ParseException e) {
            e.printStackTrace();
            model.addAttribute("error", "invalid_date");
            return false;
        }
    }

    public void makeReservation(HttpSession session) {
        Reservation res = (Reservation) session.getAttribute("reservation");
        Vehicle vehicle = res.getVehicle();
        vehicle.setAvailable(false);
        vehicleService.saveVehicle(vehicle);
        reservationRepository.save(res);
        User user = userService.getSessionUser(session);
        String username = user.getName();
        String emailAddress = user.getEmail();

        // send confirmation mail
        mail.sendEmail(username, emailAddress, MailType.Registration);
    }
}
