package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.repository.UserRepository;
import com.codecool.carshare.repository.VehicleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VehicleServiceTest {

    @Autowired
    VehicleService vehicleService;

    @MockBean
    VehicleRepository vehicleRepository;

    @MockBean
    HttpSession session;

    @MockBean
    User mockUser;

    @MockBean
    UserRepository userRepository;

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

    @Test
    public void testUploadVehicleReturnsMapWithUserAttribute() {
        String mockUsername = "username";
        when(session.getAttribute("user")).thenReturn(mockUsername);
        when(userRepository.getUserByName(mockUsername)).thenReturn(mockUser);

        Map params = vehicleService.uploadVehicle("name", "2004", "4", "type", "", "2017-11-11", "2017-11-13", "Butapest", session);

        User actualUser = (User) params.get("user");
        assertTrue(actualUser.equals(mockUser));
    }

    @Test
    public void testUploadVehicleReturnsErrorMessageIfNotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);
        Map params = vehicleService.uploadVehicle("name", "2004", "4", "type", "", "2017-11-11", "2017-11-13", "Butapest", session);

        String actualErrorMessage = (String) params.get("error");
        String expectedErrorMessage = "not_logged_in";

        assertTrue(actualErrorMessage.equals(expectedErrorMessage));
    }

    @Test
    public void testUploadVehicleSavesVehicleInRepoOnHappyPath() {
        String mockUsername = "username";
        when(session.getAttribute("user")).thenReturn(mockUsername);
        when(userRepository.getUserByName(mockUsername)).thenReturn(mockUser);

        vehicleService.uploadVehicle("name", "2004", "4", "type", "", "2017-11-11", "2017-11-13", "Butapest", session);

        verify(vehicleRepository, atLeastOnce()).save((Vehicle) any());
    }
}