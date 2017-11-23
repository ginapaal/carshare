package com.codecool.carshare.controller;

import com.codecool.carshare.model.*;
import com.codecool.carshare.model.email.ReservationMail;
import com.codecool.carshare.model.email.WelcomeMail;
import com.codecool.carshare.model.User;
import com.codecool.carshare.model.UserProfilePicture;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.model.email.Mail;
import com.codecool.carshare.utility.DataManager;
import com.codecool.carshare.utility.SecurePassword;
import org.thymeleaf.exceptions.TemplateProcessingException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import javax.persistence.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PageController {

    private UserProfilePicture profilePictureLink;
    private String emailAddress;

    private DataManager dataManager;
    private Mail welcomeMail;
    private Mail reservationMail;
    private SecurePassword securePassword;


    private Map<String, Object> params;

    public Map getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public PageController(DataManager dataManager, Mail welcomeMail, Mail reservationMail, SecurePassword securePassword) {
        this.dataManager = dataManager;
        this.welcomeMail = welcomeMail;
        this.reservationMail = reservationMail;
        this.securePassword = securePassword;
    }

    public String renderVehicles(Request req, Response res) {
        params = new HashMap<>();
        String filterString = req.queryParams("type");
        VehicleType type = VehicleType.getTypeFromString(filterString);
        List results = dataManager.getVehicleListByType(type);

        String username = req.session().attribute("user");
        if (username != null) {
            User user = dataManager.getUserByName(username);
            params.put("user", user);
        }
        params.put("types", Arrays.asList(VehicleType.values()));
        params.put("vehicles", results);
        if (type != null) {
            params.put("selected", type);
        } else {
            params.put("selected", "");
        }

        return renderTemplate(params, "index");
    }

    public String details(Request req, Response res) {
        Map<String, Object> params = new HashMap<>();
        int vehicleId = Integer.valueOf(req.params("id"));

        String username = req.session().attribute("user");
        if (username != null) {
            User user = dataManager.getUserByName(username);
            params.put("user", user);
            if (user != null) {
                emailAddress = user.getEmail();
            }
        }

        Vehicle resultVehicle = dataManager.getVehicleById(vehicleId);

        if (req.requestMethod().equalsIgnoreCase("POST")) {
            if (username == null) {
                res.redirect("/login");
            }

            String resStartDate = req.queryParams("reservation_startdate");
            String resEndDate = req.queryParams("reservation_enddate");
            Date startDateRes = new Date();
            Date endDateRes = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                startDateRes = df.parse(resStartDate);
                endDateRes = df.parse(resEndDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            User user = dataManager.getUserByName(username);
            Reservation reservation = new Reservation(resultVehicle, user, startDateRes, endDateRes);
            if (!resultVehicle.setReservation(startDateRes, endDateRes)) {
                dataManager.persist(reservation);
                dataManager.update(resultVehicle);
                reservationMail.sendEmail(emailAddress, username);
            }

            res.redirect("/");
        }

        params.put("vehicle", resultVehicle);

        return renderTemplate(params, "details");
    }

    public String register(Request req, Response res) throws InvalidKeySpecException, NoSuchAlgorithmException {
        params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {
            String username = req.queryParams("username").toLowerCase().trim();
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            String confirmPassword = req.queryParams("confirm-password");

            if (username.equals("") || email.equals("") || password.equals("") ||
                    confirmPassword.equals("")) {
                System.out.println("One ore more field is empty");
                params.put("errorMessage", "All fields are required");
                params.put("username", username);
                params.put("email", email);

                return renderTemplate(params, "register");
            }

            String passwordHash = securePassword.createHash(password);

            if (password.equals(confirmPassword)) {
                User user = new User(username, email, passwordHash);
                dataManager.persist(user);

                // send welcome mail to registered e-mail address
                welcomeMail.sendEmail(email, username);

                return loginUser(req, res, username);
            } else {
                params.put("errorMessage", "Confirm password");
                params.put("username", username);
                params.put("email", email);
                params.put("focus", "password");
            }
        }

        return renderTemplate(params, "register");
    }

    public String login(Request req, Response res) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (userLoggedIn(req, res)) return "";

        Map<String, Object> params = new HashMap<>();
        if (req.requestMethod().equalsIgnoreCase("POST")) {
            String name = convertField(req.queryParams("username"));
            String password = req.queryParams("password");

            String storedPassword;

            if (password.equals("") || name.equals("")) {
                System.out.println("One ore more field is empty");
                params.put("errorMessage", "All fields are required");
                return renderTemplate(params, "login");
            } else {
                storedPassword = dataManager.getPasswordByName(name);
            }

            if (storedPassword != null && securePassword.isPasswordValid(password, storedPassword)) {
                return loginUser(req, res, name);
            } else {
                params.put("errorMessage", "Invalid username or password");
            }
        }

        return renderTemplate(params, "login");
    }

    public String uploadVehicle(Request req, Response res) {
        params = new HashMap<>();
        String username = req.session().attribute("user");
        if (username != null) {
            User user = dataManager.getUserByName(username);
            params.put("user", user);
        } else {
            res.redirect("/");
        }
        params.put("profilePicture", profilePictureLink);

        if (req.requestMethod().equalsIgnoreCase("POST")) {
            String name = req.queryParams("name");
            String year = req.queryParams("year");
            String seats = req.queryParams("numofseats");
            String type = req.queryParams("type");
            String piclink = req.queryParams("piclink");
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            VehicleType vehicleType = VehicleType.getTypeFromString(type);

            int yearInt = Integer.parseInt(year);
            int numOfSeats = Integer.parseInt(seats);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date startDateF = df.parse(startDate);
                Date endDateF = df.parse(endDate);
                Vehicle vehicle = new Vehicle(name, yearInt, numOfSeats, vehicleType, piclink, startDateF, endDateF);
                // sets owner to uploaded car
                User owner = dataManager.getUserByName(username);
                vehicle.setOwner(owner);
                owner.addVehicle(vehicle);
                vehicle.setAvailability();
                dataManager.persist(vehicle);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            res.redirect("/user/" + dataManager.getUserByName(username).getId());
        }

        return renderTemplate(params, "upload");
    }

    public String profile(Request req, Response res) {
        HashMap<String, Object> params = new HashMap<>();
        String username = req.session().attribute("user");
        User user = dataManager.getUserByName(username);
        String profilePicLink = "";
        int userId;
        if (user != null) {
            userId = user.getId();
            if (!(Integer.parseInt(req.params("id")) == userId)) {
                res.redirect("/user/" + userId);
                return "";
            }
            params.put("uploadpage", true);
            if (user.getUserProfilePicture() != null) {
                profilePicLink = user.getUserProfilePicture().getProfilePicture();
            }
        } else {
            res.redirect("/");
            return "";
        }
        if (req.requestMethod().equalsIgnoreCase("POST")) {
            String profilePicture = req.queryParams("profilePicture");
            if (!profilePicture.equals("") && !profilePicture.equals(profilePicLink)) {
                UserProfilePicture userProfilePicture = new UserProfilePicture(profilePicture);
                userProfilePicture.setUser(user);
                dataManager.persist(userProfilePicture);
            }
            res.redirect("/user/" + userId);
            return "";
        }
        try {
            UserProfilePicture profilePicture = dataManager.getUserProfilePictureById(userId);
            params.put("profilePicture", profilePicture);
            profilePictureLink = profilePicture;
        } catch (NoResultException e) {
            UserProfilePicture defaultPicture = new UserProfilePicture();
            params.put("profilePicture", defaultPicture);
            profilePictureLink = defaultPicture;
        }
        params.put("user", user);
        return renderTemplate(params, "userProfile");
    }

    public String logout(Request req, Response res) {
        System.out.println(req.session().attribute("user") + " logged out");
        req.session().removeAttribute("user");
        res.redirect("/login");
        return "";
    }

    private String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
    }

    private String convertField(String string) {
        return string.toLowerCase().trim().replaceAll("\\s+", "");
    }

    private boolean userLoggedIn(Request req, Response res) {
        if (req.session().attribute("user") != null) {
            System.out.println(req.session().attribute("user") + " are already logged in");
            res.redirect("/");
            return true;
        }
        return false;
    }

    private String loginUser(Request req, Response res, String name) {
        req.session().attribute("user", name);
        System.out.println(req.session().attribute("user") + " logged in");
        res.redirect("/");
        return "";
    }

}