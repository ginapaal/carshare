package com.codecool.carshare.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Error implements ErrorController {
    private String errorpath = "/error";

    @RequestMapping(value="/error")
    public String error() {
        return "error";
    }

    @Override
    public String getErrorPath() {
        return errorpath;
    }
}

