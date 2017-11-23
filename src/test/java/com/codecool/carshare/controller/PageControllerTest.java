package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.UserProfilePicture;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PageControllerTest {

    private PageController pageController;
    private DataManager dataManager;
    private Request request;
    private Response res;
    private Mail welcomeMail;
    private Mail reservationMail;
    private SecurePassword securePassword;
    private User user = new User();
    private UserProfilePicture profilePicture;

    @BeforeEach
    void setUp() {
        request = mock(Request.class, RETURNS_DEEP_STUBS);
        res = mock(Response.class);

        dataManager = mock(DataManager.class);
        welcomeMail = mock(WelcomeMail.class);
        reservationMail = mock(ReservationMail.class);
        securePassword = mock(SecurePassword.class);
        profilePicture = mock(UserProfilePicture.class);
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

        assertEquals("gergo", userName);
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
    void testLoginRedirectsToRootIfAlreadyLoggedIn() throws InvalidKeySpecException, NoSuchAlgorithmException {
        when(request.session().attribute("user")).thenReturn(new User());
        pageController.login(request, res);
        verify(res, times(1)).redirect("/");
    }

    @Test
    void testLoginSendsErrorMessageIfPasswordIsEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("password")).thenReturn("");
        when(request.queryParams("username")).thenReturn("binladen");
        when(request.session().attribute("user")).thenReturn(null);
        String expectedErrorMessage = "All fields are required";

        pageController.login(request, res);

        Map testData = pageController.getParams();
        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
    }

    @Test
    void testLoginSendsErrorMessageIfUsernameIsEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("password")).thenReturn("binladen");
        when(request.queryParams("username")).thenReturn("");
        when(request.session().attribute("user")).thenReturn(null);
        String expectedErrorMessage = "All fields are required";

        pageController.login(request, res);

        Map testData = pageController.getParams();
        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
    }

    @Test
    void testLoginSendsErrorMessageIfPasswordIsInvalid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("password")).thenReturn("a");
        when(request.queryParams("username")).thenReturn("b");
        when(request.session().attribute("user")).thenReturn(null);

        when(securePassword.isPasswordValid(anyString(), anyString())).thenReturn(false);
        when(dataManager.getPasswordByName("b")).thenReturn("not null");
        String expectedErrorMessage = "Invalid username or password";

        pageController.login(request, res);

        Map testData = pageController.getParams();
        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
    }

    @Test
    void testLoginRedirectsToRootAfterValidLogin() throws InvalidKeySpecException, NoSuchAlgorithmException {
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("username")).thenReturn("b");
        when(request.queryParams("password")).thenReturn("a");
        when(request.session().attribute("user")).thenReturn(null);

        when(dataManager.getPasswordByName(anyString())).thenReturn("not null");
        when(securePassword.isPasswordValid(anyString(), anyString())).thenReturn(true);

        pageController.login(request, res);
        verify(res, times(1)).redirect("/");
    }

    @Test
    void testProfileRedirectsToLoggedInUsersProfileIfCalledWithDifferentId() {
        when(request.session().attribute("user")).thenReturn("gergo");
        user.setId(10); //currently logged in user has ID 10
        when(dataManager.getUserByName("gergo")).thenReturn(user);
        when(request.params("id")).thenReturn("1"); //requesting the profile page of ID 1
        pageController.profile(request, res);
        verify(res, times(1)).redirect("/user/10");
    }

    @Test
    void testProfileRedirectsToRootIfNotLoggedIn() {
        when(request.session().attribute("user")).thenReturn(null);
        when(dataManager.getUserByName(anyString())).thenReturn(null);
        pageController.profile(request, res);
        verify(res, times(1)).redirect("/");
    }

    @Test
    void testProfileStoresProfilePictureOnUploadIfNotEmpty() {
        when(request.session().attribute("user")).thenReturn("irrelevant");
        when(request.params("id")).thenReturn("0");
        when(dataManager.getUserByName("irrelevant")).thenReturn(user);
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("profilePicture")).thenReturn("some link for a profile pic");
        pageController.profile(request, res);
        verify(dataManager, times(1)).persist(any());
    }

    @Test
    void testProfileRedirectsToProfilePageAfterUpload() {
        when(request.session().attribute("user")).thenReturn("irrelevant");
        when(request.params("id")).thenReturn("0");
        when(dataManager.getUserByName("irrelevant")).thenReturn(user);
        when(request.requestMethod()).thenReturn("POST");
        when(request.queryParams("profilePicture")).thenReturn("some link for a profile pic");
        pageController.profile(request, res);
        verify(res, times(1)).redirect("/user/0");
    }
}