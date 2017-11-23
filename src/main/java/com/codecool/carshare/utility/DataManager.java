package com.codecool.carshare.utility;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.UserProfilePicture;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.codecool.carshare.model.VehicleType.*;

public class DataManager {

    private EntityManager em;
    private SecurePassword securePassword;

    public DataManager(EntityManager em, SecurePassword securePassword){
        this.em = em;
        this.securePassword = securePassword;
    }

    public List getVehicleListByType(VehicleType type) {
        List results;
        if (type != null) {
            results = em.createNamedQuery("Vehicle.getByType", Vehicle.class)
                    .setParameter("type", type).getResultList();
        } else {
            results = em.createNamedQuery("Vehicle.getAll", Vehicle.class).getResultList();
        }
        return results;
    }

    public Vehicle getVehicleById(int vehicleId) {
        Vehicle vehicle = em.createNamedQuery("Vehicle.getById", Vehicle.class)
                .setParameter("vehicleId", vehicleId).getSingleResult();
        return vehicle;
    }

    public String getPasswordByName(String name) {
        try {
            String storedPassword = (String) em.createNamedQuery("User.getPasswordHash")
                    .setParameter("name", name)
                    .getSingleResult();
            return storedPassword;
        } catch (NoResultException e) {
            System.out.println("No such a user in db");
        }
        return null;
    }

    public User getUserByName(String name) {
        try {
            User user = (User) em.createNamedQuery("User.getUserByName")
                    .setParameter("name", name)
                    .getSingleResult();
            return user;
        } catch (NoResultException e) {
            System.out.println("No such a user in db");
        }
        return null;
    }

    public UserProfilePicture getUserProfilePictureById(int userId) {
        UserProfilePicture profilePicture = em.createNamedQuery("getUsersProfPic", UserProfilePicture.class)
                .setParameter("user_id", userId)
                .getSingleResult();
        return profilePicture;
    }

    public void persist(Object object) {
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(object);
        transaction.commit();
    }

    public void update(Object objectToUpdate) {
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.merge(objectToUpdate);
        transaction.commit();
    }

    public void populateTestData() throws InvalidKeySpecException, NoSuchAlgorithmException {
        User owner = new User("gergo", "valami@valami.com", securePassword.createHash("pass"));
        User owner2 = new User("Ödönke", "odon@tokodon.hu", securePassword.createHash("odon"));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = df.parse("2017-11-10");
            endDate = df.parse("2017-12-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Vehicle vehicle = new Vehicle("Ödönke kocsija", 1978, 3, Car, "https://www.alamo.com/alamoData/vehicle/bookingCountries/US/CARS/SSAR.doi.320.high.imageLargeThreeQuarterNodePath.png/1508943174788.png",
                startDate, endDate);
        Vehicle vehicle1 = new Vehicle("Ödönke másik kocsija", 1990, 4, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-Aston-Martin-V12-Zagato-Red-Sports-Car-PNG-Image.png",
                startDate, endDate);
        Vehicle vehicle2 = new Vehicle("Ödönke harmadik kocsija", 1990, 4, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-Bugatti-Chiron-Blue-Car-PNG-Image.png",
                startDate, endDate);
        Vehicle vehicle3 = new Vehicle("Ödönke negyedik kocsija", 1990, 4, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-BMW-M2-Coupe-White-Car-PNG-Image.png",
                startDate, endDate);
        owner.addVehicle(vehicle1);
        owner.addVehicle(vehicle2);
        owner.addVehicle(vehicle3);
        vehicle.setOwner(owner);
        vehicle1.setOwner(owner);
        vehicle2.setOwner(owner2);
        vehicle3.setOwner(owner2);
        persist(owner);
        persist(owner2);
        persist(vehicle);
        persist(vehicle1);
        persist(vehicle2);
        persist(vehicle3);

        persist(new Vehicle("Egy motor", 2005, 1, Motor, "http://pngimg.com/uploads/motorcycle/motorcycle_PNG5341.png",
                startDate, endDate));
        persist(new Vehicle("Egy másik motor", 2009, 1, Motor, "http://pngimg.com/uploads/motorcycle/motorcycle_PNG3150.png",
                startDate, endDate));
        persist(new Vehicle("Batmobile", 1960, 2, Car, "https://cache.popcultcha.com.au/media/catalog/product/cache/1/image/1800x/040ec09b1e35df139433887a97daa66f/m/e/metals-batman-v-superman-batmobile-4-inch-01_1.1498484298.png",
                startDate, endDate));
        persist(new Vehicle("Kitt", 1980, 4, Car, "http://www.hotwheels-elite.com/diecast-model-cars/images/Image/CATALOGO-A-2010/1-18%20CULT%20CLASSIC%20COLLECTION/X5469%20-%20KITT/X5469_PoP_13_12_w900.png",
                startDate, endDate));
        persist(new Vehicle("Bobby's first bike", 2002, 1, Bike, "http://www.straydogbicycles.com/images/Specialized_Hotrock.png",
                startDate, endDate));

    }
}
