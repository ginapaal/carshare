package com.codecool.carshare.controller;

import com.codecool.carshare.model.Customer;
import com.codecool.carshare.model.SecurePassword;
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
            String password = req.queryParams("password");

            Customer user = new Customer(username, email, password);
            persist(user);

            return renderTemplate(params, "login");
        }

        return renderTemplate(params, "register");
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

    private static String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
    }
}