package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.UserProfilePicture;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.utility.DataManager;
import com.codecool.carshare.utility.SecurePassword;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PageController {

    public static String renderVehicles(Request req, Response res) {

        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        String filterString = req.queryParams("type");
        VehicleType type = VehicleType.getTypeFromString(filterString);
        List results;
        if (type != null) {
            Query filterQuery = em.createNamedQuery("Vehicle.getByType", Vehicle.class).setParameter("type", type);
            results = filterQuery.getResultList();
        } else {
            results = em.createNamedQuery("Vehicle.getAll", Vehicle.class).getResultList();
        }
        HashMap<String, List> params = new HashMap<>();
        params.put("types", Arrays.asList(VehicleType.values()));
        params.put("vehicles", results);

        em.close();

        return renderTemplate(params, "index");
    }

    public static String register(Request req, Response res) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        Map<String, String> params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {

            String username = req.queryParams("username").toLowerCase().trim();
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            String confirmPassword = req.queryParams("confirm-password");

            if (username.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
                System.out.println("One ore more field is empty");
                params.put("errorMessage", "All fields are required");
                params.put("username", username);
                params.put("email", email);
                return renderTemplate(params, "register");
            }

            String passwordHash = SecurePassword.createHash(password);

            if (password.equals(confirmPassword)) {
                User user = new User(username, email, passwordHash);
                persist(user);
                return renderTemplate(params, "login");
            } else {
                params.put("errorMessage", "Confirm password");
                params.put("username", username);
                params.put("email", email);
                params.put("focus", "password");
            }
        }

        return renderTemplate(params, "register");
    }

    public static String uploadVehicle(Request req, Response res) {
        Map params = new HashMap();
        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        String userName = req.session().attribute("user");
        User user = em.createNamedQuery("User.getSpecUser", User.class).setParameter("name", userName).getSingleResult();


        params.put("user", user);

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
            int numofSeats = Integer.parseInt(seats);


            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date startDateF = df.parse(startDate);
                Date endDateF = df.parse(endDate);
                Vehicle vehicle = new Vehicle(name, yearInt, numofSeats, vehicleType, piclink, startDateF, endDateF);
                vehicle.setOwner(user);
                persist(vehicle);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            res.redirect("/profile");
        }
        return renderTemplate(params, "upload");
    }

    public static String login(Request req, Response res) throws InvalidKeySpecException, NoSuchAlgorithmException {

        String name = req.queryParams("username");
        String password = req.queryParams("password");

        if (req.session().attribute("user") != null) {
            System.out.println(req.session().attribute("user") + " are already logged in");
            res.redirect("/");
            return "";
        }

        Map<String, String> params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {

            String storedPassword;

            if (password.equals("")) {
                System.out.println("username or password is null - redirect");
                res.redirect("/login");
                return "";
            } else {
                storedPassword = getPasswordByName(name);
            }

            if (storedPassword != null && SecurePassword.validatePassword(password, storedPassword)) {
                req.session().attribute("user", name);
                System.out.println(req.session().attribute("user") + " logged in");
                res.redirect("/");
                return "";
            }
        }

        return renderTemplate(params, "login");
    }

    public static String owner(Request request, Response response) {
        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        HashMap<String, Object> params = new HashMap<>();
        String userName = request.session().attribute("user");
        User result = em.createNamedQuery("User.getSpecUser", User.class).setParameter("name", userName).getSingleResult();
        int userId = result.getId();

        if (request.requestMethod().equalsIgnoreCase("POST")) {
            String profilePicture = request.queryParams("profilePicture");
            UserProfilePicture userProfilePicture = new UserProfilePicture(profilePicture);
            userProfilePicture.setUser(result);
            persist(userProfilePicture);
        }
        try {
            UserProfilePicture profilePicture = em.createNamedQuery("getUsersProfPic", UserProfilePicture.class).setParameter("user_id", userId).getSingleResult();
            params.put("profilePicture", profilePicture);
        } catch (NoResultException e) {
            UserProfilePicture defaultPicture = new UserProfilePicture();
            defaultPicture.setProfilePicture("/default_pic.jpg");
            params.put("profilePicture", defaultPicture);
        }
        params.put("user", result);
        return renderTemplate(params, "userProfile");
    }

    public static String logout(Request req, Response res) {
        System.out.println(req.session().attribute("user") + " logged out");
        req.session().removeAttribute("user");
        res.redirect("/login");
        return "";
    }

    private static String getPasswordByName(String name) {
        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            String storedPassword = (String) em.createNamedQuery("User.getPasswordHash")
                    .setParameter("name", name)
                    .getSingleResult();
            em.close();
            return storedPassword;
        } catch (NoResultException e) {
            System.out.println("No such a user in db");
        }
        return null;
    }

    private static User getUserByName(String name) {
        EntityManager em = DataManager.getEntityManagerFactory().createEntityManager();
        try {
            User user = (User) em.createNamedQuery("User.getSpecUser")
                    .setParameter("name", name)
                    .getSingleResult();
            em.close();
            return user;
        } catch (NoResultException e) {
            System.out.println("No such a user in db");
        }
        return null;
    }


    private static void persist(Object object) {
        EntityManagerFactory emf = DataManager.getEntityManagerFactory();

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(object);
        transaction.commit();
        em.close();
    }

    private static String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
    }
}