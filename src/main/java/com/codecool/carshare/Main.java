package com.codecool.carshare;

import com.codecool.carshare.controller.PageController;
import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.utility.DataManager;
import com.codecool.carshare.utility.SecurePassword;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.codecool.carshare.model.VehicleType.*;
import static spark.Spark.*;

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
        get("/users/:id", PageController::owner);

        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        populateTestData(em);

    }

    public static void populateTestData(EntityManager entityManager) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User owner = new User("user", "user@user.user", SecurePassword.createHash("jelszo"));
        Vehicle vehicle = new Vehicle();
        owner.addVehicle(vehicle);
        vehicle.setOwner(owner);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(new Vehicle("Egy motor", 2005, 1, Motor, "https://auto.ndtvimg.com/car-images/medium/audi/sq7/audi-sq7.jpg", "nice motor"));
        entityManager.persist(new Vehicle("Egy másik motor", 2009, 1, Motor, "https://auto.ndtvimg.com/car-images/medium/ferrari/gtc4lusso/ferrari-gtc4lusso.jpg", "not as nice motor"));
        entityManager.persist(new Vehicle("Batmobile", 1960, 2, Car, "https://img.autobytel.com/car-reviews/autobytel/11694-good-looking-sports-cars/2016-Ford-Mustang-GT-burnout-red-tire-smoke.jpg", "NANANANANANANANANA"));
        entityManager.persist(new Vehicle("Kitt", 1980, 4, Car, "https://auto.ndtvimg.com/car-images/big/dc/avanti/dc-avanti.jpg", "THE FKIN KNIGHT RIDER"));
        entityManager.persist(new Vehicle("Bobby's first bike", 2002, 1, Bike, "https://auto.ndtvimg.com/car-images/medium/maruti-suzuki/alto-800/maruti-suzuki-alto-800.jpg", "Its pretty shitty."));
        entityManager.persist(owner);
        entityManager.persist(vehicle);

        transaction.commit();
    }
}
