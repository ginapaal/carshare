package com.codecool.carshare;

import com.codecool.carshare.controller.PageController;
import com.codecool.carshare.model.email.ReservationMail;
import com.codecool.carshare.model.email.WelcomeMail;
import com.codecool.carshare.utility.DataManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class CarShareApp {

    private DataManager dataManager;
    private PageController pageController;

    public CarShareApp(DataManager dataManager, PageController pageController) {
        this.dataManager = dataManager;
        this.pageController = pageController;
    }

    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        DataManager dataManager = new DataManager();
        PageController pageController = new PageController(dataManager, new WelcomeMail(), new ReservationMail());

        CarShareApp app = new CarShareApp(dataManager, pageController);

        app.start();
    }

    public void start() throws InvalidKeySpecException, NoSuchAlgorithmException {
        dataManager.populateTestData();
        startServer(pageController);
    }

    public void startServer(PageController pageController) {
        //default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);
        enableDebugScreen();

        //routes
        get("/register", pageController::register);
        post("/register", pageController::register);
        get("/login", pageController::login);
        post("/login", pageController::login);
        get("/logout", pageController::logout);
        get("/upload", pageController::uploadVehicle);
        post("/upload", pageController::uploadVehicle);
        get("/user/:id", pageController::profile);
        post("/upload-profile-pic", pageController::profile);
        get("/vehicles/:id", pageController::details);
        post("/vehicles/:id", pageController::details);
        get("/", pageController::renderVehicles);
    }

}
