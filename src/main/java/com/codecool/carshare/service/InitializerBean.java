package com.codecool.carshare.service;

import com.codecool.carshare.model.Vehicle;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codecool.carshare.model.VehicleType.Car;

@Component
public class InitializerBean {

    public InitializerBean(VehicleService vehicleService) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = df.parse("2017-11-10");
            endDate = df.parse("2017-12-07");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        vehicleService.saveVehicle(new Vehicle("Dodge Challenger", 1978, 3, Car, "https://www.alamo.com/alamoData/vehicle/bookingCountries/US/CARS/SSAR.doi.320.high.imageLargeThreeQuarterNodePath.png/1508943174788.png",
                startDate, endDate));
    }
}
