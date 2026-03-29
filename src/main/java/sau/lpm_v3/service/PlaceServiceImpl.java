package sau.lpm_v3.service;

import sau.lpm_v3.dtos.PlaceDTO;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.Place;
import sau.lpm_v3.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public PlaceDTO getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id)).viewAsPlaceDTO();
    }

    public Place getPlaceEntityById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id));
    }

    public List<PlaceDTO> getAllPlaces() {
        return placeRepository.findAll().stream().map(Place::viewAsPlaceDTO).toList();
    }

    public PlaceDTO createPlace(PlaceDTO placeDto) {
        // Convert DTO to Entity Internally
        Place place = placeDto.toEntity();

        if (placeRepository.findById(placeDto.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_PLACE_ALREADY_EXIST + ": " + placeDto.getId());
        }
        return placeRepository.save(place).viewAsPlaceDTO();
    }

    public PlaceDTO updatePlace(Long id, PlaceDTO placeDto) {
        // Convert DTO to Entity Internally
        Place place = placeDto.toEntity();

        placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id));
        place.setId(id);
        return placeRepository.save(place).viewAsPlaceDTO();
    }

    public void deletePlace(Long id) {
        placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id));
        placeRepository.deleteById(id);
    }

}