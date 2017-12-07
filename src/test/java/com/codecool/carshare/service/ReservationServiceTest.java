package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.model.Vehicle;
import com.codecool.carshare.repository.ReservationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ReservationServiceTest {

    @MockBean
    Model model;

    @MockBean
    HttpSession session;

    @MockBean
    Mail mail;

    @MockBean
    ReservationRepository reservationRepository;

    @MockBean
    VehicleService vehicleService;

    @MockBean
    UserService userService;

    @MockBean
    Vehicle vehicle;

    @Autowired
    ReservationService reservationService;

    @Test
    public void testReserveVehicleReturnsFalseIfNotLoggedIn() {
        assertFalse(
                reservationService.reserveVehicle(model, session, "1", "", "")
        );
    }

    @Test
    public void testReserveVehicleStoresProperErrorMessageIfNotLoggedIn() {
        reservationService.reserveVehicle(model, session, "1", "not valid", "not valid");
        verify(model).addAttribute("error", "not_logged_in");
    }

    @Test
    public void testReserveVehicleReturnsFalseIfInvalidDate() {
        assertFalse(
                reservationService.reserveVehicle(model, session, "1", "not valid", "not valid")
        );
    }

    @Test
    public void testReserveVehicleStoresProperErrorMessageIfInvalidDate() {
        when(session.getAttribute("user")).thenReturn("username");
        reservationService.reserveVehicle(model, session, "1", "not valid", "not valid");
        verify(model).addAttribute("error", "invalid_date");
    }

    @Test
    public void testReserveVehicleStoresReservationInModelAndSession() {
        when(session.getAttribute("user")).thenReturn("username");
        when(vehicleService.findVehicleById(anyInt())).thenReturn(vehicle);
        when(vehicle.setReservation(any(), any())).thenReturn(true);

        reservationService.reserveVehicle(model, session, "1", "2017-11-11", "2017-11-12");
        verify(session).setAttribute(eq("reservation"), any());
        verify(model).addAttribute(eq("reservation"), any());
    }

    @Test
    public void testMakeReservationSendsEmailProperly() {
        User mockUser = mock(User.class);
        String mockUsername = "username";
        String mockEmail = "emailaddress";

        when(userService.getSessionUser(session)).thenReturn(mockUser);
        when(mockUser.getName()).thenReturn(mockUsername);
        when(mockUser.getEmail()).thenReturn(mockEmail);

        reservationService.makeReservation(session);

        verify(mail).sendEmail(mockUsername, mockEmail, MailType.Registration);
    }
}
