package com.codecool.carshare.service;

import com.codecool.carshare.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
}