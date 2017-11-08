package com.codecool.carshare.utility;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataManager {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("carsharePU");
        }
        return emf;
    }
}
