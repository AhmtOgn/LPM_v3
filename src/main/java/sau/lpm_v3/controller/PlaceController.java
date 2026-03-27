package sau.lpm_v3.controller;

import sau.lpm_v3.dtos.PlaceDTO;
import sau.lpm_v3.model.Place;
import sau.lpm_v3.service.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place")
public class PlaceController {
    private final static Logger logger = LoggerFactory.getLogger(PlaceController.class);
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {this.placeService = placeService;}

    @GetMapping("/all")
    public ResponseEntity<List<PlaceDTO>> getAllPlaces() {
        logger.info("Get all place");
        return new ResponseEntity<>(placeService.getAllPlaces(), HttpStatus.OK);
    }
    @GetMapping(value = "/get/{id}", produces = "application/json")
    public ResponseEntity<PlaceDTO> getPlace(@PathVariable Long id) {
        if (id == null || id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        logger.info("Get place by id {}", id);
        return new ResponseEntity<>(placeService.getPlaceById(id), HttpStatus.OK);
    }
    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PlaceDTO> addPlace(@RequestBody Place place) {
        logger.info("Creating place {}", place.getId());
        return new ResponseEntity<>(placeService.createPlace(place), HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<PlaceDTO> updatePlace(@PathVariable Long id, @RequestBody Place place) {
        if (id == null || id == 0 || place == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        logger.info("Updating place {}", id);
        return new ResponseEntity<>(placeService.updatePlace(id, place), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Place> deletePlace(@PathVariable Long id) {
        if (id == null || id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        placeService.deletePlace(id);
        logger.info("Deleting place {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
