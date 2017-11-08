package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.utility.SecurePassword;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import javax.persistence.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageController {

    public static String renderVehicles(Request req, Response res) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();

        String filterString = req.queryParams("type");
        VehicleType type = VehicleType.getTypeFromString(filterString);
        List results;
        if (type != null) {
            Query filterQuery = em.createNamedQuery("Vehicle.getByType", Vehicle.class).setParameter("type", type);
            results = filterQuery.getResultList();
        }
        else {
            results = em.createNamedQuery("Vehicle.getAll", Vehicle.class).getResultList();
        }
        HashMap<String, List> params = new HashMap<>();
        params.put("types", Arrays.asList(VehicleType.values()));
        params.put("vehicles", results);

        em.close();
        emf.close();

        return renderTemplate(params, "index");
    }

    public static String register(Request req, Response res) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        Map<String, String> params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {

            if (req.queryParams("password") == null) {
                System.out.println("password is null");
                return renderTemplate(params, "register");
            }

            String username = req.queryParams("username");
            String email = req.queryParams("email");
            String passwordHash = SecurePassword.createHash(req.queryParams("password"));

            User user = new User(username, email, passwordHash);
            persist(user);

            return renderTemplate(params, "login");
        }

        return renderTemplate(params, "register");
    }

    public static String uploadVehicle(Request req, Response res) {
        Map params = new HashMap();

        if (req.requestMethod().equalsIgnoreCase("POST")) {
            String name = req.queryParams("name");
            String year = req.queryParams("year");
            String seats = req.queryParams("numofseats");
            String type = req.queryParams("type");
            System.out.println(type);
            String description = req.queryParams("description");
            String piclink = req.queryParams("piclink");

            VehicleType vehicleType = VehicleType.getTypeFromString(type);

            int yearInt = Integer.parseInt(year);
            int numofSeats = Integer.parseInt(seats);

            Vehicle vehicle = new Vehicle(name, yearInt, numofSeats, vehicleType, piclink);
            persist(vehicle);

            res.redirect("/profile");
        }

        return renderTemplate(params, "upload");
    }

    public static String login(Request req, Response res) throws InvalidKeySpecException, NoSuchAlgorithmException {

        String name = req.queryParams("username");
        String password = req.queryParams("password");

        if (req.session().attribute("user") != null) {
            System.out.println(req.session().attribute("user") + " are already logged in");
            res.redirect("/a");
            return "";
        }

        Map<String, String> params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {

            String storedPassword = getPasswordByName(name);

            if (password.equals("")) {
                System.out.println("username or password is null - redirect");
                res.redirect("/login");
                return "";
            }

            if (SecurePassword.validatePassword(password, storedPassword)) {
                req.session().attribute("user", name);
                res.redirect("/a");
                return "";
            }
        }

        return renderTemplate(params, "login");
    }

    public static String logout(Request req, Response res) {
        System.out.println(req.session().attribute("user") + " logged out");
        req.session().removeAttribute("user");
        res.redirect("/login");
        return "";
    }

    private static String getPasswordByName(String name) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        String storedPassword = (String) em.createQuery("SELECT passwordHash FROM User WHERE name = :name")
                .setParameter("name", name)
                .getSingleResult();
        em.close();
        emf.close();
        return storedPassword;
    }


    private static void persist(Object object) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(object);
        transaction.commit();
        em.close();
        emf.close();
    }

//    private static void persistV(Vehicle vehicle) {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction transaction = em.getTransaction();
//
//        transaction.begin();
//        em.persist(vehicle);
//        transaction.commit();
//        em.close();
//        emf.close();
//    }

    private static String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
    }

    public static String owner(Request request, Response response) {
        return renderTemplate(new HashMap(), "userProfile");
    }
}