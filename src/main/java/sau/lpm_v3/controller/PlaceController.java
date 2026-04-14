package sau.lpm_v3.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import sau.lpm_v3.dtos.PlaceDTO;
import sau.lpm_v3.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

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
    public String addPlace(@ModelAttribute("place") PlaceDTO placeDto, Authentication auth) {
        log.info("[USER: {}] ACTION: Adding new place: [Building: {}, Room: {}]",
                auth.getName(), placeDto.getBuilding(), placeDto.getRoom());

        placeService.createPlace(placeDto);
        return "redirect:/place/all";
    }

    @GetMapping("/update/{id}")
    public String updatePlace(@PathVariable Long id, Model model) {
        model.addAttribute("place", placeService.getPlaceById(id));
        return "places/_update";
    }

    @PostMapping("/update")
    public String updatePlace(@ModelAttribute("place") PlaceDTO placeDto, Authentication auth) {
        log.info("[USER: {}] ACTION: Updating place ID: [{}]",
                auth.getName(), placeDto.getId());

        placeService.updatePlace(placeDto.getId(), placeDto);
        return "redirect:/place/all";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deletePlace(@PathVariable Long id, Authentication auth) {
        log.warn("[USER: {}] ACTION: Deleting place ID: [{}]",
                auth.getName(), id);

        placeService.deletePlace(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}