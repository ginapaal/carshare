package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.utility.SecurePassword;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import javax.persistence.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class PageController {

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

    public static String login(Request req, Response res) throws InvalidKeySpecException, NoSuchAlgorithmException {

        Map<String, String> params = new HashMap<>();

        if (req.requestMethod().equalsIgnoreCase("POST")) {

            String name = req.queryParams("username");
            String password = req.queryParams("password");
            String storedPassword = getPasswordByName(name);

            if (password.equals("")) {
                System.out.println("username or password is null - redirect");
                res.redirect("/login");
                return "";
            }

            if (SecurePassword.validatePassword(password, storedPassword)) {
                res.redirect("/a");
                return "";
            }
        }

        return renderTemplate(params, "login");
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


    private static void persist(User user) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("carsharePU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(user);
        transaction.commit();
        em.close();
        emf.close();
    }

    private static String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
    }
}