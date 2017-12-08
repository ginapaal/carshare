package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.repository.UserRepository;
import com.codecool.carshare.utility.SecurePassword;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    Model model;

    @MockBean
    Mail mail;

    @MockBean
    InitializerBean initializerBean;

    @MockBean
    HttpSession session;

    @MockBean
    SecurePassword securePassword;

    @MockBean
    ReservationService reservationService;

    @MockBean
    VehicleService vehicleService;

    @Test
    public void testRegisterIfEveryInputIsOK() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "gergo";
        String email = "valami@valami.com";
        String password = "pass";
        String confirmPassword = "pass";

        userService.register(username, password, confirmPassword, email, model);

        verify(mail).sendEmail("gergo", "valami@valami.com", MailType.Welcome);
    }

    @Test
    public void testRegisterIfConfirmPassIsDifferentThanPass() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "gergo";
        String email = "valami@valami.com";
        String password = "pass";
        String confirmPassword = "notpass";
        Model model = new ExtendedModelMap();

        userService.register(username, password, confirmPassword, email, model);

        assertEquals("Passwords don't match!", model.asMap().get("errorMessage"));
    }

    @Test
    public void testLoginSendsErrorMessageIfPasswordIsEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "binladen";
        String password = "";
        String expectedErrorMessage = "All fields are required";

        Map testData = userService.login(username, password, session);

        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
    }

    @Test
    public void testLoginSendsErrorMessageIfUsernameIsEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "";
        String password = "password";
        String expectedErrorMessage = "All fields are required";

        Map testData = userService.login(username, password, session);

        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
    }

    @Test
    public void testLoginSendsErrorMessageIfPasswordIsInvalid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "a";
        String password = "b";
        String expectedErrorMessage = "Invalid username or password";
        User user = mock(User.class);

        when(securePassword.isPasswordValid(anyString(), anyString())).thenReturn(false);
        when(userRepository.getUserByName(anyString())).thenReturn(user);
        when(user.getPasswordHash()).thenReturn("any");

        Map testData = userService.login(username, password, session);

        assertEquals(expectedErrorMessage, testData.get("errorMessage"));
    }
//
    @Test
    public void testLoginIfPasswordIsValid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "a";
        String password = "b";
        User user = mock(User.class);

        when(userRepository.getUserByName(anyString())).thenReturn(user);
        when(user.getPasswordHash()).thenReturn("any");
        when(securePassword.isPasswordValid(anyString(), anyString())).thenReturn(true);

        Map testData = userService.login(username, password, session);

        assertEquals(null, testData.get("errorMessage"));
        assertEquals(username, testData.get("user"));
    }
}