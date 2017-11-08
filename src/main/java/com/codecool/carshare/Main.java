package com.codecool.carshare;

import com.codecool.carshare.controller.PageController;
import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import static com.codecool.carshare.model.VehicleType.Bike;
import static com.codecool.carshare.model.VehicleType.Car;
import static com.codecool.carshare.model.VehicleType.Motor;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        //default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        get("/a", (Request req, Response res) -> new ThymeleafTemplateEngine().render(new ModelAndView(new HashMap<>(), "index")));
        get("/register", PageController::register);
        post("/register", PageController::register);
        get("/login", PageController::login);
        post("/login", PageController::login);
        get("/logout", PageController::logout);
        get("/", PageController::renderVehicles);
        get("/upload", PageController::uploadVehicle);
        post("/upload", PageController::uploadVehicle);
        get("/profile", PageController::owner);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        populateTestData(em);

    }

    public static void populateTestData(EntityManager entityManager) {
        User owner = new User();
        Vehicle vehicle = new Vehicle();
        owner.addVehicle(vehicle);
        vehicle.setOwner(owner);


        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(new Vehicle("Egy motor", 2005, 1, Motor, "link"));
        entityManager.persist(new Vehicle("Egy másik motor", 2009, 1, Motor, "link"));
        entityManager.persist(new Vehicle("Batmobile", 1960, 2, Car, "link"));
        entityManager.persist(new Vehicle("Kitt", 1980, 4, Car, "link"));
        entityManager.persist(new Vehicle("Bobby's first bike", 2002, 1, Bike, "link"));
        entityManager.persist(owner);
        entityManager.persist(vehicle);

        transaction.commit();
    }
}
