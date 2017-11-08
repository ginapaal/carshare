package com.codecool.carshare.controller;

import com.codecool.carshare.model.Customer;
import com.codecool.carshare.utility.SecurePassword;
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
import java.util.Map;

public class PageController {

    public static String register(Request req, Response res) throws IOException {

        Map<String, String> params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {

            if (req.queryParams("password") == null) {
                System.out.println("password is null");
                return renderTemplate(params, "register");
            }

            String username = req.queryParams("username");
            String email = req.queryParams("email");
            String passwordHash = securePassword(req.queryParams("password"));

            Customer user = new Customer(username, email, passwordHash);
            persist(user);

            return renderTemplate(params, "login");
        }

        return renderTemplate(params, "register");
    }

    private static String securePassword(String password) {
        String passwordHash;
        try {
            passwordHash = SecurePassword.generateStrongPasswordHash(password);
            return passwordHash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void persist(Customer user) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(user);
        transaction.commit();
        em.close();
        emf.close();
    }

    public static String login(Request req, Response res) {

        return renderTemplate(new HashMap(), "login");
    }

    private static String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
    }
}