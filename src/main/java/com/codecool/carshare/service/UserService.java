package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.repository.UserRepository;
import com.codecool.carshare.utility.SecurePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurePassword securePassword;

    public Map<String, Object> login(String username, String password, HttpSession session) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Map<String, Object> params = new HashMap<>();
        String name = convertField(username);

        String storedPassword;

        if (password.equals("") || name.equals("")) {
            System.out.println("One ore more field is empty");
            params.put("errorMessage", "All fields are required");
            return params;
        } else {
            storedPassword = userRepository.getUserByName(username).getPasswordHash();
        }

        if (storedPassword != null && securePassword.isPasswordValid(password, storedPassword)) {
            session.setAttribute("user", username);
            params.put("user", username);
        } else {
            params.put("errorMessage", "Invalid username or password");
        }

        return params;
    }

    private String convertField(String string) {
        return string.toLowerCase().trim().replaceAll("\\s+", "");
    }

    public boolean register(String username, String password, String confirmPassword, String email, Model model) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords don't match!");
            return false;
        }

        try {
            userRepository.save(new User(username, email, securePassword.createHash(password)));
            return true;
        } catch (DataIntegrityViolationException e) { // what kind of exception is this gonna throw?
            model.addAttribute("errorMessage", "Username or email already exists!");
            e.printStackTrace();
            return false;
        }
    }

    public Object getSessionUser(HttpSession session) {
        User user = userRepository.getUserByName((String) session.getAttribute("user"));
        return user;
    }

    public void saveUser(User entity) {
        userRepository.save(entity);
    }

}
