package com.codecool.carshare.service;

import com.codecool.carshare.model.User;
import com.codecool.carshare.repository.UserRepository;
import com.codecool.carshare.utility.SecurePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public Map<String, Object> registration(String username,
                                            String email,
                                            String password,
                                            String confirmPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, Object> params = new HashMap<>();
        if (username.equals("") || email.equals("") || password.equals("") ||
                confirmPassword.equals("")) {
            System.out.println("One ore more field is empty");
            params.put("errorMessage", "All fields are required");
            params.put("username", username);
            params.put("email", email);
//            return params;
        }

        String passwordHash = securePassword.createHash(password);

        if (password.equals(confirmPassword)) {
            User user = new User(username, email, passwordHash);
            saveUser(user);

//                // send welcome mail to registered e-mail address
//                welcomeMail.sendEmail(email, username);

//                return loginUser(req, res, username);
        } else {
            params.put("errorMessage", "Confirm password");
            params.put("username", username);
            params.put("email", email);
            params.put("focus", "password");
//            return params;

        }
//        System.out.println(params);
        return params;
    }


    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findUserById(Integer id) {
        return userRepository.findUserById(id);
    }
}
