package com.codecool.carshare;

import com.codecool.carshare.controller.PageController;
import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.utility.DataManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.codecool.carshare.model.VehicleType.*;
import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {

    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        //default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        //routes
        get("/register", PageController::register);
        post("/register", PageController::register);
        get("/login", PageController::login);
        post("/login", PageController::login);
        get("/logout", PageController::logout);
        get("/", PageController::renderVehicles);
        get("/upload", PageController::uploadVehicle);
        post("/upload", PageController::uploadVehicle);
        get("/profile", PageController::owner);

        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        populateTestData(em);

        enableDebugScreen();

    }

    public static void populateTestData(EntityManager entityManager) {
        User owner = new User("Ödönke", "odon@tokodon.hu", "pw");
        Vehicle vehicle = new Vehicle("Ödönke kocsija", 1978, 3, Car, "kép");
        Vehicle vehicle1 = new Vehicle("Ödönke másik kocsija", 1990, 6, Car, "kép");
        Vehicle vehicle2 = new Vehicle("Ödönke harmadik kocsija", 1990, 6, Car, "kép");
        owner.addVehicle(vehicle);
        owner.addVehicle(vehicle1);
        owner.addVehicle(vehicle2);
        vehicle.setOwner(owner);
        vehicle1.setOwner(owner);
        vehicle2.setOwner(owner);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(new Vehicle("Egy motor", 2005, 1, Motor, "link"));
        entityManager.persist(new Vehicle("Egy másik motor", 2009, 1, Motor, "link"));
        entityManager.persist(new Vehicle("Batmobile", 1960, 2, Car, "link"));
        entityManager.persist(new Vehicle("Kitt", 1980, 4, Car, "link"));
        entityManager.persist(new Vehicle("Bobby's first bike", 2002, 1, Bike, "link"));
        entityManager.persist(owner);
        entityManager.persist(vehicle);
        entityManager.persist(vehicle1);
        entityManager.persist(vehicle2);

        transaction.commit();
    }
}
