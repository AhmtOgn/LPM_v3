package sau.lpm_v3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sau.lpm_v3.dtos.PlaceDTO;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.Place;
import sau.lpm_v3.repository.PlaceRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public PlaceDTO getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id))
                .viewAsPlaceDTO();
    }

    public Place getPlaceEntityById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id));
    }

    @Override
    public List<PlaceDTO> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(Place::viewAsPlaceDTO)
                .toList();
    }

    @Override
    public PlaceDTO createPlace(PlaceDTO placeDto) {
        if (placeDto.getId() != 0 && placeRepository.findById(placeDto.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_PLACE_ALREADY_EXIST + ": " + placeDto.getId());
        }

        Place place = placeDto.toEntity();
        Place savedPlace = placeRepository.save(place);

        log.info("SERVICE: New place created with ID: [{}]", savedPlace.getId());
        return savedPlace.viewAsPlaceDTO();
    }

    @Override
    public PlaceDTO updatePlace(Long id, PlaceDTO placeDto) {
        placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id));

        Place place = placeDto.toEntity();
        place.setId(id);

        log.info("SERVICE: Updating place record for ID: [{}]", id);
        return placeRepository.save(place).viewAsPlaceDTO();
    }

    @Override
    public void deletePlace(Long id) {
        placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PLACE_NOT_FOUND + ": " + id));

        placeRepository.deleteById(id);
        log.warn("SERVICE: Deleted place record with ID: [{}]", id);
    }
}