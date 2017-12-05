package com.codecool.carshare.service;

import com.codecool.carshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> login(String username, String password, HttpSession session) throws InvalidKeySpecException, NoSuchAlgorithmException {
//        if (userLoggedIn(req, res)) return "";

        Map<String, Object> params = new HashMap<>();
        String name = convertField(username);

        String storedPassword;

        if (password.equals("") || name.equals("")) {
            System.out.println("One ore more field is empty");
            params.put("errorMessage", "All fields are required");
            return params;
        } else {
            storedPassword = dataManager.getPasswordByName(name);
        }

        if (storedPassword != null && securePassword.isPasswordValid(password, storedPassword)) {
            return loginUser(req, res, name);
        } else {
            params.put("errorMessage", "Invalid username or password");
        }

        return renderTemplate(params, "login");
    }

//    private boolean userLoggedIn(Request req, Response res) {
//        if (req.session().attribute("user") != null) {
//            System.out.println(req.session().attribute("user") + " are already logged in");
//            res.redirect("/");
//            return true;
//        }
//        return false;
//    }

    private String convertField(String string) {
        return string.toLowerCase().trim().replaceAll("\\s+", "");
    }
}
