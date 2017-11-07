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

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException {
        //default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        //call root
        get("/", (Request req, Response res) -> new ThymeleafTemplateEngine().render(new ModelAndView(new HashMap<>(), "index")));

        Vehicle vehicle = new Vehicle();
        Vehicle vehicle1 = new Vehicle("Jármű2");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(vehicle);
        em.persist(vehicle1);
        transaction.commit();
        em.close();
        emf.close();
    }
}
