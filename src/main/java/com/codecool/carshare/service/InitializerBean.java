package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.utility.SecurePassword;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codecool.carshare.model.VehicleType.Bike;
import static com.codecool.carshare.model.VehicleType.Car;
import static com.codecool.carshare.model.VehicleType.Motor;

@Component
public class InitializerBean {

    public InitializerBean(VehicleService vehicleService, UserService userService,
                           SecurePassword securePassword)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = df.parse("2017-11-10");
            endDate = df.parse("2017-12-07");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Vehicle vehicle1 = new Vehicle("Dodge Challenger", 1978, 3, Car, "https://www.alamo.com/alamoData/vehicle/bookingCountries/US/CARS/SSAR.doi.320.high.imageLargeThreeQuarterNodePath.png/1508943174788.png",
                startDate, endDate, "Budapest");
        Vehicle vehicle2 = new Vehicle("Aston Martin", 1990, 4, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-Aston-Martin-V12-Zagato-Red-Sports-Car-PNG-Image.png",
                startDate, endDate, "Budapest");
        Vehicle vehicle3 = new Vehicle("Bugatti Chiron", 1990, 4, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-Bugatti-Chiron-Blue-Car-PNG-Image.png",
                startDate, endDate, "Szeged");
        Vehicle vehicle4 = new Vehicle("BMW M2 Coupe", 1990, 4, Car, "http://www.pngpix.com/wp-content/uploads/2016/06/PNGPIX-COM-BMW-M2-Coupe-White-Car-PNG-Image.png",
                startDate, endDate, "Budapest");
        Vehicle vehicle5 = new Vehicle("Suzuki XRV-100", 2005, 1, Motor, "http://pngimg.com/uploads/motorcycle/motorcycle_PNG5341.png",
                startDate, endDate, "Jászkarajenő");
        Vehicle vehicle6 = new Vehicle("Suzuki GSX", 2009, 1, Motor, "http://pngimg.com/uploads/motorcycle/motorcycle_PNG3150.png",
                startDate, endDate, "Győr");
        Vehicle vehicle7 = new Vehicle("Batmobile", 1960, 2, Car, "https://cache.popcultcha.com.au/media/catalog/product/cache/1/image/1800x/040ec09b1e35df139433887a97daa66f/m/e/metals-batman-v-superman-batmobile-4-inch-01_1.1498484298.png",
                startDate, endDate, "Pécs");
        Vehicle vehicle8 = new Vehicle("Kitt", 1980, 4, Car, "http://www.hotwheels-elite.com/diecast-model-cars/images/Image/CATALOGO-A-2010/1-18%20CULT%20CLASSIC%20COLLECTION/X5469%20-%20KITT/X5469_PoP_13_12_w900.png",
                startDate, endDate, "Nyíregyháza");
        Vehicle vehicle9 = new Vehicle("Bobby's first bike", 2002, 1, Bike, "http://www.straydogbicycles.com/images/Specialized_Hotrock.png",
                startDate, endDate, "Szolnok");

        User user1 = new User("gergo", "valami@valami.com", securePassword.createHash("pass"));
        User user2 = new User("Ödönke", "odon@tokodon.hu", securePassword.createHash("odon"));

        vehicleService.saveVehicle(vehicle1);
        vehicleService.saveVehicle(vehicle2);
        vehicleService.saveVehicle(vehicle3);
        vehicleService.saveVehicle(vehicle4);
        vehicleService.saveVehicle(vehicle5);
        vehicleService.saveVehicle(vehicle6);
        vehicleService.saveVehicle(vehicle7);
        vehicleService.saveVehicle(vehicle8);
        vehicleService.saveVehicle(vehicle9);
        userService.saveUser(user1);
        userService.saveUser(user2);
    }
}
