package be.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/site")
    public String index(Model model) {
        model.addAttribute("message", "Hello world message!");
        return "index";
    }
}
