package sau.lpm_v3.controller;

import sau.lpm_v3.dtos.PlaceDTO;
import sau.lpm_v3.service.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/place")
public class PlaceController {
    private final static Logger logger = LoggerFactory.getLogger(PlaceController.class);
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("all")
    public String getAllPlaces(Model model) {
        List<PlaceDTO> placeDtos = placeService.getAllPlaces();
        model.addAttribute("places", placeDtos);
        return "places/all";
    }
    @GetMapping(value = "/{id}")
    public String getPlace(@PathVariable Long id, Model model) {
        model.addAttribute("place", placeService.getPlaceById(id));
        return "places/_show";
    }

    @GetMapping(value = "/add")
    public String addPlace(Model model) {
        model.addAttribute("place", new PlaceDTO());
        return "places/_add";
    }

    @PostMapping("/add")
    public String addPlace(@ModelAttribute("place") PlaceDTO placeDto) {
        // Logs when add a new entity
        // It is going to add USER DETAILS who performed to action
        logger.info("A new Place that ID is [{}] ADDED", placeDto.getId());

        // Converting operating made internally
        placeService.createPlace(placeDto);
        return "redirect:/place/all";
    }

    @GetMapping("/update/{id}")
    public String updatePlace(@PathVariable Long id, Model model) {
        // Already getPlaceById converts DTO
        model.addAttribute("place", placeService.getPlaceById(id));
        return "places/_update";
    }

    @PostMapping("/update")
    public String updatePlace(@ModelAttribute("place") PlaceDTO placeDto) {
        // Logs when update an entity
        // It is going to add USER DETAILS who performed to action
        logger.info("Place that ID is [{}] UPDATED", placeDto.getId());

        // Converting operating made internally
        placeService.updatePlace(placeDto.getId(), placeDto);
        return "redirect:/place/all";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePlace(@PathVariable Long id) {
        // Logs when delete an entity
        // It is going to add USER DETAILS who performed to action
        logger.warn("Place with ID is [{}] DELETED", id);

        placeService.deletePlace(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
