package cond.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/create")
@RequiredArgsConstructor
public class CreateController {

    @GetMapping()
    public String create() {
        return "create";
    }
}