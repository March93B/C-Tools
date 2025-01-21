package cond.code.controllers;

import cond.code.entities.Seeker;
import cond.code.services.SeekerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/seeker")
public class SeekerController {

    private final SeekerService seekerService;
    public SeekerController(SeekerService seekerService) {
        this.seekerService = seekerService;

    }
    @GetMapping()
    public ModelAndView seeker() {
        ModelAndView mave = new ModelAndView("seeker");
        List<Seeker> blackducks = seekerService.getSeekers();
        mave.addObject("blackducks", blackducks);
        return mave;
    }

}
