package de.htw.berlin.webtech.recipe_manager.web;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import de.htw.berlin.webtech.recipe_manager.domain.Unit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meta")
public class MetaController {

    @GetMapping("/units")
    public Unit[] units() {
        return Unit.values();
    }

    @GetMapping("/diet-tags")
    public DietTag[] dietTags() {
        return DietTag.values();
    }
}

//mit den methoden laden sie dynamisch