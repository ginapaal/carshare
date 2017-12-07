package com.codecool.carshare.service;

import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.repository.VehicleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VehicleServiceTest {

    @Autowired
    VehicleService vehicleService;

    @MockBean
    VehicleRepository vehicleRepository;

    @Test
    public void testRenderVehiclesVehicleTypeDoesNotExist() {
        String filterType = "truck";

        when(vehicleRepository.findAll()).thenReturn(null);
        Map testData = vehicleService.renderVehicles(filterType);

        String filteredType = (String) testData.get("selected");

        assertEquals("", filteredType);
    }

    @Test
    public void testRenderVehiclesVehicleTypeReturnsAsExpected() {
        String filterType = "Car";
        VehicleType vehicleType = VehicleType.Car;

        when(vehicleRepository.findVehicleByVehicleType(vehicleType)).thenReturn(null);
        Map testData =vehicleService.renderVehicles(filterType);
        VehicleType filteredType = (VehicleType) testData.get("selected");

        assertEquals(vehicleType, filteredType);
    }

}