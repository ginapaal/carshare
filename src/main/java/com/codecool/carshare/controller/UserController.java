package com.codecool.carshare.controller;

import com.codecool.carshare.model.User;
import com.codecool.carshare.service.UserService;
import com.codecool.carshare.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpSession session) {
        User user = userService.getSessionUser(session);
        if (user != null) return "redirect:/";

        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPage(Model model,
                            HttpSession session,
                            @RequestParam("username") String username,
                            @RequestParam("password") String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        model.addAllAttributes(userService.login(username, password, session));
        return "redirect:/";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(Model model, HttpSession session,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("email") String email,
                               @RequestParam("confirm-password") String confirmPassword)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (userService.register(username, password, confirmPassword, email, model)) {
            session.setAttribute("user", username);
            return "redirect:/";
        } else {
            return "/register";
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public String renderProfilePage(Model model, HttpSession session) {
        User user = userService.getSessionUser(session);
        if (user == null) return "redirect:/";
        model.addAttribute("user", user);
        model.addAttribute("uploadpage", true);
        return "userProfile";
    }

    @RequestMapping(value = "/vehicles/{id}/reservation", method = RequestMethod.GET)
    public String billingInfoPage(HttpSession session, Model model,
                                  @PathVariable("id") String vehicleId) {
        if (session.getAttribute("reservation") != null) {
            model.addAttribute("user", userService.getSessionUser(session));
            model.addAttribute("vehicle", vehicleService.findVehicleById(Integer.valueOf(vehicleId)));
            model.addAttribute("reservation", session.getAttribute("reservation"));
            return "billing";
        }
        else {
            return "redirect:/vehicles/" + vehicleId;
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadVehiclePage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("user");
        if (username != null) {
            User user = userService.getSessionUser(session);
            model.addAttribute("user", user);
        } else {
            return "redirect:/";
        }
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadVehicle(Model model, HttpSession session,
                                @RequestParam("name") String name,
                                @RequestParam("year") String year,
                                @RequestParam("numofseats") String seats,
                                @RequestParam("type") String type,
                                @RequestParam("piclink") String piclink,
                                @RequestParam("startDate") String startDate,
                                @RequestParam("endDate") String endDate,
                                @RequestParam("location") String location) {
        model.addAllAttributes(vehicleService.uploadVehicle(name, year, seats, type, piclink, startDate, endDate, location, session));
        User user = userService.getSessionUser(session);
        model.addAttribute("user", user);

        model.addAttribute("profilePicture", user.getProfilePicture());
        return "redirect:" + "/user/" + user.getId();
    }

    @RequestMapping(value = "/{id}/upload-profile-pic", method = RequestMethod.POST)
    public String uploadProfilePicture(HttpSession session,
                                       @PathVariable("id") String id,
                                       @RequestParam("profilePicture") String profilePicture) {
        userService.uploadProfilePicture(userService.getSessionUser(session), profilePicture);
        return "redirect:/user/" + id;
    }
}
