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

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VehicleServiceTest {

    @Autowired
    VehicleService vehicleService;

    @MockBean
    UserService userService;

    @MockBean
    VehicleRepository vehicleRepository;

    @MockBean
    HttpSession session;

    @MockBean
    User mockUser;

    @MockBean
    UserRepository userRepository;

    @MockBean
    Mail mail;

    @MockBean
    ReservationService reservationService;

    @MockBean
    InitializerBean initializerBean;

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
        Map testData = vehicleService.renderVehicles(filterType);
        VehicleType filteredType = (VehicleType) testData.get("selected");

        assertEquals(vehicleType, filteredType);
    }

    @Test
    public void testDetailsUserIsNull() {
        String mockId = "9";

        when(session.getAttribute("user")).thenReturn(null);

        Map testData = vehicleService.details(mockId, session);

        assertFalse(testData.containsKey("user"));
    }

    @Test
    public void testDetailsReturnsExpectedName() {
        String mockId = "9";
        String mockUsername = "joe";
        User mockUser = new User();
        mockUser.setName(mockUsername);

        when(session.getAttribute("user")).thenReturn(mockUsername);
        when(userService.getUserByName(mockUsername)).thenReturn(mockUser);

        Map testData = vehicleService.details(mockId, session);
        User testUser = (User) testData.get("user");
        String testUserName = testUser.getName();

        assertEquals(testUserName, mockUsername);
    }

    @Test
    public void testDetailsReturnsExpectedReservationStartDate() {
        String mockId = "9";
        String mockUsername = "joe";
        User mockUser = new User();
        mockUser.setName(mockUsername);
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(Integer.valueOf(mockId));

        when(session.getAttribute("user")).thenReturn(mockUsername);
        when(userService.getUserByName(mockUsername)).thenReturn(mockUser);
        when(vehicleRepository.findVehicleById(any())).thenReturn(mockVehicle);

        Map testData = vehicleService.details(mockId, session);
        Vehicle testVehicle = (Vehicle) testData.get("vehicle");

        assertEquals(testVehicle.getId(), mockVehicle.getId());
    }


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

