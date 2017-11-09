package com.codecool.carshare.utility;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.UserProfilePicture;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;

import javax.persistence.*;
import java.util.List;

public class DataManager {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("carsharePU");
        }
        return emf;
    }

    public static List getVehicleListByType(VehicleType type) {
        EntityManager em = emf.createEntityManager();
        List results;
        if (type != null) {
            results = em.createNamedQuery("Vehicle.getByType", Vehicle.class)
                    .setParameter("type", type).getResultList();
        } else {
            results = em.createNamedQuery("Vehicle.getAll", Vehicle.class).getResultList();
        }
        em.close();
        return results;
    }

    public static Vehicle getVehicleById(int vehicleId) {
        EntityManager em = emf.createEntityManager();
        Vehicle vehicle = em.createNamedQuery("Vehicle.getById", Vehicle.class)
                .setParameter("vehicleId", vehicleId).getSingleResult();
        em.close();
        return vehicle;
    }

    public static String getPasswordByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            String storedPassword = (String) em.createNamedQuery("User.getPasswordHash")
                    .setParameter("name", name)
                    .getSingleResult();
            em.close();
            return storedPassword;
        } catch (NoResultException e) {
            System.out.println("No such a user in db");
        }
        return null;
    }

    public static User getUserByName(String name) {
        EntityManager em = DataManager.getEntityManagerFactory().createEntityManager();
        try {

            User user = (User) em.createNamedQuery("User.getUserByName")
                    .setParameter("name", name)
                    .getSingleResult();
            em.close();
            return user;
        } catch (NoResultException e) {
            System.out.println("No such a user in db");
        }
        return null;
    }

    public static UserProfilePicture getUserProfilePictureById(int userId) {
        EntityManager em = emf.createEntityManager();
        UserProfilePicture profilePicture = em.createNamedQuery("getUsersProfPic", UserProfilePicture.class)
                .setParameter("user_id", userId)
                .getSingleResult();
        em.close();
        return profilePicture;
    }

    public static void persist(Object object) {
        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(object);
        transaction.commit();
        em.close();
    }
}
