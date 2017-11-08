package com.codecool.carshare;

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
import java.util.HashMap;

import static com.codecool.carshare.model.VehicleType.Bike;
import static com.codecool.carshare.model.VehicleType.Car;
import static com.codecool.carshare.model.VehicleType.Motor;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        populateTestData(entityManager);

        //default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        //call root
        get("/", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(VehicleController.renderVehicles(entityManager, req.queryParams("type")));
        });

    }

    public static void populateTestData(EntityManager entityManager) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(new Vehicle("Egy motor", 2005, 1, Motor));
        entityManager.persist(new Vehicle("Egy másik motor", 2009, 1, Motor));
        entityManager.persist(new Vehicle("Batmobile", 1960, 2, Car));
        entityManager.persist(new Vehicle("Kitt", 1980, 4, Car));
        entityManager.persist(new Vehicle("Bobby's first bike", 2002, 1, Bike));
        transaction.commit();
    }
}
