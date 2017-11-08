package com.codecool.carshare;

import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import spark.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class VehicleController {

    public static ModelAndView renderVehicles(EntityManager entityManager, String filterString) {

        VehicleType type = VehicleType.getTypeFromString(filterString);
        List results;
        if (type != null) {
            Query filterQuery = entityManager.createNamedQuery("Vehicle.getByType", Vehicle.class).setParameter("type", type);
            results = filterQuery.getResultList();
        }
        else {
            results = entityManager.createNamedQuery("Vehicle.getAll", Vehicle.class).getResultList();
        }
        HashMap<String, List> params = new HashMap<>();
        params.put("types", Arrays.asList(VehicleType.values()));
        params.put("vehicles", results);

        return new ModelAndView(params, "index");
    }
}
