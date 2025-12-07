package com.realestate.realestate.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactForwardController {

    @GetMapping(value = {
            "/admin",
            "/admin/**",
            "/dashboard",
            "/projects",
            "/properties",
            "/enquiries"
    })
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}
