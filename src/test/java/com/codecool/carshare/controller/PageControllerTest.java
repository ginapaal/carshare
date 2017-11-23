package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.email.Mail;
import com.codecool.carshare.model.email.ReservationMail;
import com.codecool.carshare.model.email.WelcomeMail;
import com.codecool.carshare.utility.DataManager;
import com.codecool.carshare.utility.SecurePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageControllerTest {

    PageController pageController;
    DataManager dataManager;
    Request request;
    Response res;
    Mail welcomeMail;
    Mail reservationMail;
    SecurePassword securePassword;
    User user = new User();


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
        String userName = user.getName();
    }

    @Test
    void testRenderVehiclesReturnValueAsExpected() {
        when(dataManager.getUserByName("gergo")).thenReturn(user);
        when(request.session().attribute("user")).thenReturn("gergo");

        pageController.renderVehicles(request, res);

        Map testData = pageController.getParams();
        User myUser = (User) testData.get("user");
        String username =myUser.getName();

        assertEquals(username, "gergo");
    }

}