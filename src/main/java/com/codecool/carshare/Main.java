package com.codecool.carshare;

import com.codecool.carshare.controller.PageController;
import com.codecool.carshare.model.Customer;
import com.codecool.carshare.model.SecurePassword;
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

        Vehicle vehicle = new Vehicle();
        Vehicle vehicle1 = new Vehicle("Jármű2");
        Customer user = new Customer("gergo", "email@email.com", SecurePassword.generateStrongPasswordHash("aaa"));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(vehicle);
        em.persist(vehicle1);
        em.persist(user);
        transaction.commit();
        em.close();
        emf.close();
    }
}
