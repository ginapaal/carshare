package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.model.VehicleType;
import com.codecool.carshare.model.email.Mail;
import com.codecool.carshare.model.email.ReservationMail;
import com.codecool.carshare.model.email.WelcomeMail;
import com.codecool.carshare.utility.DataManager;
import com.codecool.carshare.utility.SecurePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageControllerTest {

    private PageController pageController;
    private DataManager dataManager;
    private Request request;
    private Response res;
    private Mail welcomeMail;
    private Mail reservationMail;
    private SecurePassword securePassword;
    private User user = new User();


    @BeforeEach
    void setUp() {
        request = mock(Request.class, RETURNS_DEEP_STUBS);
        res = mock(Response.class);

        dataManager = mock(DataManager.class);
        welcomeMail = mock(WelcomeMail.class);
        reservationMail = mock(ReservationMail.class);
        securePassword = mock(SecurePassword.class);
        pageController = new PageController(dataManager, welcomeMail, reservationMail, securePassword);
        user.setName("gergo");
    }

    @Test
    void testRenderVehiclesReturnsExpectedName() {
        when(dataManager.getUserByName("gergo")).thenReturn(user);

        when(request.session().attribute("user")).thenReturn("gergo");


        pageController.renderVehicles(request,res);

        Map testData = pageController.getParams();
        User myUser = (User) testData.get("user");
        String userName = myUser.getName();

        assertEquals(userName, "gergo");
    }

    @Test
    void testRenderVehiclesUserIsNull() {
        when(request.session().attribute("user")).thenReturn(null);

        pageController.renderVehicles(request, res);
        Map testData = pageController.getParams();

        assertFalse(testData.containsKey("user"));
    }

    @Test
    void testRenderVehiclesVehicleTypeDoesNotExist() {
        when(request.session().attribute("user")).thenReturn(null);
        when(request.queryParams("type")).thenReturn("truck");
        VehicleType vehicleType = VehicleType.getTypeFromString("truck");

        pageController.renderVehicles(request, res);
        Map testData = pageController.getParams();
        String filteredType = (String) testData.get("selected");

        assertEquals("", filteredType);
    }

    @Test
    void testRenderVehiclesVehicleTypeReturnsAsExpected() {
        when(request.session().attribute("user")).thenReturn(null);
        when(request.queryParams("type")).thenReturn("Car");

        VehicleType vehicleType = VehicleType.getTypeFromString("Car");

        pageController.renderVehicles(request, res);
        Map testData = pageController.getParams();
        VehicleType filteredType = (VehicleType) testData.get("selected");

        assertEquals(VehicleType.Car, filteredType);
    }
    
    @Test
    void testDetailsUserIsNull() {
        String stubUserName = "peti";
        user.setName(stubUserName);

        Vehicle stubVehicle = new Vehicle();
        int stubVehicleId = 9;
        stubVehicle.setId(stubVehicleId);

        when(request.params("id")).thenReturn("9");
        when(request.session().attribute("user")).thenReturn(null);
        when(dataManager.getVehicleById(stubVehicleId)).thenReturn(stubVehicle);
        when(request.requestMethod()).thenReturn("GET");

        pageController.details(request,res);

        Map testData = pageController.getParams();

        assertFalse(testData.containsKey("user"));
    }

    @Test
    void testDetailsReturnsExpectedName() {
        String stubUserName = "peti";
        user.setName(stubUserName);

        Vehicle stubVehicle = new Vehicle();
        int stubVehicleId = 9;
        stubVehicle.setId(stubVehicleId);

        when(request.params("id")).thenReturn("9");
        when(request.session().attribute("user")).thenReturn("peti");
        when(dataManager.getUserByName(stubUserName)).thenReturn(user);
        when(dataManager.getVehicleById(stubVehicleId)).thenReturn(stubVehicle);
        when(request.requestMethod()).thenReturn("GET");

        pageController.details(request,res);

        Map testData = pageController.getParams();
        User actualUser = (User) testData.get("user");
        String actualUserName = actualUser.getName();

        assertEquals(stubUserName, actualUserName);
    }

    @Test
    void testDetailsReturnsExpectedReservationStartDate() {
        String stubUserName = "peti";
        user.setName(stubUserName);

        Vehicle stubVehicle = new Vehicle();
        int stubVehicleId = 9;
        stubVehicle.setId(stubVehicleId);

        Date startDateRes = new Date();
        Date endDateRes = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDateRes = df.parse("2017-11-23");
            endDateRes = df.parse("2017-11-30");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stubVehicle.setStartDate(startDateRes);
        stubVehicle.setEndDate(endDateRes);

        when(request.params("id")).thenReturn("9");
        when(request.session().attribute("user")).thenReturn("peti");
        when(dataManager.getUserByName(stubUserName)).thenReturn(user);
        when(dataManager.getVehicleById(stubVehicleId)).thenReturn(stubVehicle);
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("reservation_startdate")).thenReturn("2017-11-23");
        when(request.queryParams("reservation_enddate")).thenReturn("2017-11-30");

        pageController.details(request,res);

        Map testData = pageController.getParams();
        Vehicle actualVehicle = (Vehicle) testData.get("vehicle");
        Date actualReservationStartDate = actualVehicle.getStartDate();

        assertEquals(stubVehicle.getStartDate(), actualReservationStartDate);
    }
}
