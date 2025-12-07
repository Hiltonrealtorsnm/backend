package com.realestate.realestate.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactForwardController {

    // ✅ Forward ALL React routes to index.html (Fixes Render 404)
    @GetMapping({
            "/admin",
            "/admin/**",

            "/dashboard",
            "/projects",
            "/project/**",
            "/properties",
            "/property/**",
            "/enquiries",
            "/enquiry/**",

            "/", 
            "/about",
            "/sell",
            "/wishlist",
            "/rent-properties",
            "/contact-agent/**"
    })
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}
