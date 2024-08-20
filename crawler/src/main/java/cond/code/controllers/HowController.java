package cond.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/how")
@RequiredArgsConstructor
public class HowController {

    @GetMapping()
    public String comoUsar(){
        return "how";
    }
}
