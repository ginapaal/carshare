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
        get("/upload", PageController::uploadVehicle);
        post("/upload", PageController::uploadVehicle);
        post("/upload-profilepic", PageController::owner);
        get("/vehicles/:id", PageController::details);
        post("/vehicles/:id", PageController::details);
        get("/user/:id", PageController::owner);
        get("/", PageController::renderVehicles);

        EntityManagerFactory emf = DataManager.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        populateTestData(em);

        enableDebugScreen();

    }

    public static void populateTestData(EntityManager entityManager) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User owner = new User("gergo", "valaki@valaki.com", SecurePassword.createHash("pass"));
        User owner2 = new User("Ödönke", "odon@tokodon.hu", SecurePassword.createHash("odon"));
        Vehicle vehicle = new Vehicle("Ödönke kocsija", 1978, 3, Car, "https://www.alamo.com/alamoData/vehicle/bookingCountries/US/CARS/SSAR.doi.320.high.imageLargeThreeQuarterNodePath.png/1508943174788.png");
        Vehicle vehicle1 = new Vehicle("Ödönke másik kocsija", 1990, 6, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-Aston-Martin-V12-Zagato-Red-Sports-Car-PNG-Image.png");
        Vehicle vehicle2 = new Vehicle("Ödönke harmadik kocsija", 1990, 6, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-Bugatti-Chiron-Blue-Car-PNG-Image.png");
        Vehicle vehicle3 = new Vehicle("Ödönke negyedik kocsija", 1990, 6, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-BMW-M2-Coupe-White-Car-PNG-Image.png");
        owner.addVehicle(vehicle1);
        owner.addVehicle(vehicle2);
        owner.addVehicle(vehicle3);
        vehicle.setOwner(owner);
        vehicle1.setOwner(owner);
        vehicle2.setOwner(owner2);
        vehicle3.setOwner(owner2);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(new Vehicle("Egy motor", 2005, 1, Motor, "http://pngimg.com/uploads/motorcycle/motorcycle_PNG5341.png"));
        entityManager.persist(new Vehicle("Egy másik motor", 2009, 1, Motor, "http://pngimg.com/uploads/motorcycle/motorcycle_PNG3150.png"));
        entityManager.persist(new Vehicle("Batmobile", 1960, 2, Car, "https://cache.popcultcha.com.au/media/catalog/product/cache/1/image/1800x/040ec09b1e35df139433887a97daa66f/m/e/metals-batman-v-superman-batmobile-4-inch-01_1.1498484298.png"));
        entityManager.persist(new Vehicle("Kitt", 1980, 4, Car, "http://www.hotwheels-elite.com/diecast-model-cars/images/Image/CATALOGO-A-2010/1-18%20CULT%20CLASSIC%20COLLECTION/X5469%20-%20KITT/X5469_PoP_13_12_w900.png"));
        entityManager.persist(new Vehicle("Bobby's first bike", 2002, 1, Bike, "http://www.straydogbicycles.com/images/Specialized_Hotrock.png"));

        entityManager.persist(owner);
        entityManager.persist(owner2);
        entityManager.persist(vehicle);
        entityManager.persist(vehicle1);
        entityManager.persist(vehicle2);
        entityManager.persist(vehicle3);

        transaction.commit();
        
    }
}
