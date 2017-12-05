//package com.codecool.carshare.controller;
//
//import com.codecool.carshare.model.User;
//import com.codecool.carshare.model.Vehicle;
//import com.codecool.carshare.model.VehicleType;
//import com.codecool.carshare.model.email.Mail;
//import com.codecool.carshare.model.email.ReservationMail;
//import com.codecool.carshare.model.email.WelcomeMail;
//import com.codecool.carshare.utility.DataManager;
//import com.codecool.carshare.utility.SecurePassword;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import spark.Request;
//import spark.Response;
//
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//class PageControllerTest {
//
//    private PageController pageController;
//    private DataManager dataManager;
//    private Request request;
//    private Response res;
//    private Mail welcomeMail;
//    private Mail reservationMail;
//    private SecurePassword securePassword;
//    private User user = new User();
//    private Vehicle vehicle;
//    private User mockedUser;
//
//
//    @BeforeEach
//    void setUp() {
//        mockedUser = mock(User.class);
//        vehicle = mock(Vehicle.class);
//        request = mock(Request.class, RETURNS_DEEP_STUBS);
//        res = mock(Response.class, RETURNS_DEEP_STUBS);
//        dataManager = mock(DataManager.class);
//        welcomeMail = mock(WelcomeMail.class);
//        reservationMail = mock(ReservationMail.class);
//        securePassword = mock(SecurePassword.class);
//
//        user.setName("gergo");
//
//        pageController = new PageController(dataManager, welcomeMail, reservationMail, securePassword);
//    }
//
//    @Test
//    void testRenderVehiclesReturnsExpectedName() {
//        when(dataManager.getUserByName("gergo")).thenReturn(user);
//        when(request.session().attribute("user")).thenReturn("gergo");
//
//        pageController.renderVehicles(request,res);
//
//        Map testData = pageController.getParams();
//        User myUser = (User) testData.get("user");
//        String userName = myUser.getName();
//
//        assertEquals("gergo", userName);
//    }
//
//    @Test
//    void testRenderVehiclesUserIsNull() {
//        when(request.session().attribute("user")).thenReturn(null);
//
//        pageController.renderVehicles(request, res);
//        Map testData = pageController.getParams();
//
//        assertFalse(testData.containsKey("user"));
//    }
//
//    @Test
//    void testRenderVehiclesVehicleTypeDoesNotExist() {
//        when(request.session().attribute("user")).thenReturn(null);
//        when(request.queryParams("type")).thenReturn("truck");
//
//        pageController.renderVehicles(request, res);
//        Map testData = pageController.getParams();
//        String filteredType = (String) testData.get("selected");
//
//        assertEquals("", filteredType);
//    }
//
//    @Test
//    void testRenderVehiclesVehicleTypeReturnsAsExpected() {
//        when(request.session().attribute("user")).thenReturn(null);
//        when(request.queryParams("type")).thenReturn("Car");
//
//        VehicleType vehicleType = VehicleType.Car;
//
//        pageController.renderVehicles(request, res);
//        Map testData = pageController.getParams();
//        VehicleType filteredType = (VehicleType) testData.get("selected");
//
//        assertEquals(vehicleType, filteredType);
//    }
//
//    @Test
//    void testRegisterIfUsernameIsEmptyString() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("username")).thenReturn("");
//        when(request.queryParams("email")).thenReturn("valami@valami.com");
//        when(request.queryParams("password")).thenReturn("pass");
//        when(request.queryParams("confirm-password")).thenReturn("pass");
//
//        pageController.register(request, res);
//        Map testData = pageController.getParams();
//
//        assertEquals("", testData.get("username"));
//    }
//
//    @Test
//    void testRegisterIfConfirmPassIsDifferentThanPass() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("username")).thenReturn("gergo");
//        when(request.queryParams("email")).thenReturn("valami@valami.com");
//        when(request.queryParams("password")).thenReturn("pass");
//        when(request.queryParams("confirm-password")).thenReturn("passs");
//
//        pageController.register(request, res);
//        Map testData = pageController.getParams();
//
//        assertEquals("Confirm password", testData.get("errorMessage"));
//    }
//
//    @Test
//    void testRegisterIfEveryInputIsOK() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("username")).thenReturn("gergo");
//        when(request.queryParams("email")).thenReturn("valami@valami.com");
//        when(request.queryParams("password")).thenReturn("pass");
//        when(request.queryParams("confirm-password")).thenReturn("pass");
//
//        pageController.register(request, res);
//
//        verify(welcomeMail).sendEmail("valami@valami.com", "gergo");
//    }
//
//    @Test
//    void testUploadVehicle() {
//        when(dataManager.getUserByName("gergo")).thenReturn(mockedUser);
//        when(request.session().attribute("user")).thenReturn("gergo");
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("name")).thenReturn("autó");
//        when(request.queryParams("year")).thenReturn("1999");
//        when(request.queryParams("numofseats")).thenReturn("6");
//        when(request.queryParams("type")).thenReturn("Car");
//        when(request.queryParams("piclink")).thenReturn("piclink");
//        when(request.queryParams("startDate")).thenReturn("11-11-2000");
//        when(request.queryParams("endDate")).thenReturn("11-11-2100");
//
//
//        when(vehicle.getName()).thenReturn("autó");
//
//        pageController.uploadVehicle(request, res);
//
//        assertEquals("autó", vehicle.getName());
//    }
//
//    @Test
//    void testUploadVehicleSetsOwnerToVehicle() {
//        when(dataManager.getUserByName("gergo")).thenReturn(mockedUser);
//        when(request.session().attribute("user")).thenReturn("gergo");
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("name")).thenReturn("autó");
//        when(request.queryParams("year")).thenReturn("1999");
//        when(request.queryParams("numofseats")).thenReturn("6");
//        when(request.queryParams("type")).thenReturn("Car");
//        when(request.queryParams("piclink")).thenReturn("piclink");
//        when(request.queryParams("startDate")).thenReturn("11-11-2000");
//        when(request.queryParams("endDate")).thenReturn("11-11-2100");
//
//        when(vehicle.getOwner()).thenReturn(mockedUser);
//        when(mockedUser.getVehicles()).thenReturn(Arrays.asList(vehicle));
//
//        pageController.uploadVehicle(request, res);
//        Map testData = pageController.getParams();
//        User userData =(User) testData.get("user");
//
//        assertEquals(mockedUser.hashCode(), userData.hashCode());
//    }
//
//    @Test
//    void testDetailsUserIsNull() {
//        String stubUserName = "peti";
//        user.setName(stubUserName);
//
//        Vehicle stubVehicle = new Vehicle();
//        int stubVehicleId = 9;
//        stubVehicle.setId(stubVehicleId);
//
//        when(request.params("id")).thenReturn("9");
//        when(request.session().attribute("user")).thenReturn(null);
//        when(dataManager.getVehicleById(stubVehicleId)).thenReturn(stubVehicle);
//        when(request.requestMethod()).thenReturn("GET");
//
//        pageController.details(request,res);
//
//        Map testData = pageController.getParams();
//
//        assertFalse(testData.containsKey("user"));
//    }
//
//    @Test
//    void testDetailsReturnsExpectedName() {
//        String stubUserName = "peti";
//        user.setName(stubUserName);
//
//        Vehicle stubVehicle = new Vehicle();
//        int stubVehicleId = 9;
//        stubVehicle.setId(stubVehicleId);
//
//        when(request.params("id")).thenReturn("9");
//        when(request.session().attribute("user")).thenReturn("peti");
//        when(dataManager.getUserByName(stubUserName)).thenReturn(user);
//        when(dataManager.getVehicleById(stubVehicleId)).thenReturn(stubVehicle);
//        when(request.requestMethod()).thenReturn("GET");
//
//        pageController.details(request,res);
//
//        Map testData = pageController.getParams();
//        User actualUser = (User) testData.get("user");
//        String actualUserName = actualUser.getName();
//
//        assertEquals(stubUserName, actualUserName);
//    }
//
//    @Test
//    void testDetailsReturnsExpectedReservationStartDate() {
//        String stubUserName = "peti";
//        user.setName(stubUserName);
//
//        Vehicle stubVehicle = new Vehicle();
//        int stubVehicleId = 9;
//        stubVehicle.setId(stubVehicleId);
//
//        Date startDateRes = new Date();
//        Date endDateRes = new Date();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            startDateRes = df.parse("2017-11-23");
//            endDateRes = df.parse("2017-11-30");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        stubVehicle.setStartDate(startDateRes);
//        stubVehicle.setEndDate(endDateRes);
//
//        when(request.params("id")).thenReturn("9");
//        when(request.session().attribute("user")).thenReturn("peti");
//        when(dataManager.getUserByName(stubUserName)).thenReturn(user);
//        when(dataManager.getVehicleById(stubVehicleId)).thenReturn(stubVehicle);
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("reservation_startdate")).thenReturn("2017-11-23");
//        when(request.queryParams("reservation_enddate")).thenReturn("2017-11-30");
//
//        pageController.details(request,res);
//
//        Map testData = pageController.getParams();
//        Vehicle actualVehicle = (Vehicle) testData.get("vehicle");
//        Date actualReservationStartDate = actualVehicle.getStartDate();
//
//        assertEquals(stubVehicle.getStartDate(), actualReservationStartDate);
//    }
//
//    @Test
//    void testLoginRedirectsToRootIfAlreadyLoggedIn() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.session().attribute("user")).thenReturn(new User());
//        pageController.login(request, res);
//        verify(res, times(1)).redirect("/");
//    }
//
//    @Test
//    void testLoginSendsErrorMessageIfPasswordIsEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("password")).thenReturn("");
//        when(request.queryParams("username")).thenReturn("binladen");
//        when(request.session().attribute("user")).thenReturn(null);
//        String expectedErrorMessage = "All fields are required";
//
//        pageController.login(request, res);
//
//        Map testData = pageController.getParams();
//        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
//    }
//
//    @Test
//    void testLoginSendsErrorMessageIfUsernameIsEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("password")).thenReturn("binladen");
//        when(request.queryParams("username")).thenReturn("");
//        when(request.session().attribute("user")).thenReturn(null);
//        String expectedErrorMessage = "All fields are required";
//
//        pageController.login(request, res);
//
//        Map testData = pageController.getParams();
//        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
//    }
//
//    @Test
//    void testLoginSendsErrorMessageIfPasswordIsInvalid() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("password")).thenReturn("a");
//        when(request.queryParams("username")).thenReturn("b");
//        when(request.session().attribute("user")).thenReturn(null);
//
//        when(securePassword.isPasswordValid(anyString(), anyString())).thenReturn(false);
//        when(dataManager.getPasswordByName("b")).thenReturn("not null");
//        String expectedErrorMessage = "Invalid username or password";
//
//        pageController.login(request, res);
//
//        Map testData = pageController.getParams();
//        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
//    }
//
//    @Test
//    void testLoginRedirectsToRootAfterValidLogin() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("username")).thenReturn("b");
//        when(request.queryParams("password")).thenReturn("a");
//        when(request.session().attribute("user")).thenReturn(null);
//
//        when(dataManager.getPasswordByName(anyString())).thenReturn("not null");
//        when(securePassword.isPasswordValid(anyString(), anyString())).thenReturn(true);
//
//        pageController.login(request, res);
//        verify(res, times(1)).redirect("/");
//    }
//
//    @Test
//    void testProfileRedirectsToLoggedInUsersProfileIfCalledWithDifferentId() {
//        when(request.session().attribute("user")).thenReturn("gergo");
//        user.setId(10); //currently logged in user has ID 10
//        when(dataManager.getUserByName("gergo")).thenReturn(user);
//        when(request.params("id")).thenReturn("1"); //requesting the profile page of ID 1
//        pageController.profile(request, res);
//        verify(res, times(1)).redirect("/user/10");
//    }
//
//    @Test
//    void testProfileRedirectsToRootIfNotLoggedIn() {
//        when(request.session().attribute("user")).thenReturn(null);
//        when(dataManager.getUserByName(anyString())).thenReturn(null);
//        pageController.profile(request, res);
//        verify(res, times(1)).redirect("/");
//    }
//
//    @Test
//    void testProfileStoresProfilePictureOnUploadIfNotEmpty() {
//        when(request.session().attribute("user")).thenReturn("irrelevant");
//        when(request.params("id")).thenReturn("0");
//        when(dataManager.getUserByName("irrelevant")).thenReturn(user);
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("profilePicture")).thenReturn("some link for a profile pic");
//        pageController.profile(request, res);
//        verify(dataManager, times(1)).persist(any());
//    }
//
//    @Test
//    void testProfileRedirectsToProfilePageAfterUpload() {
//        when(request.session().attribute("user")).thenReturn("irrelevant");
//        when(request.params("id")).thenReturn("0");
//        when(dataManager.getUserByName("irrelevant")).thenReturn(user);
//        when(request.requestMethod()).thenReturn("POST");
//        when(request.queryParams("profilePicture")).thenReturn("some link for a profile pic");
//        pageController.profile(request, res);
//        verify(res, times(1)).redirect("/user/0");
//    }
//}
//
