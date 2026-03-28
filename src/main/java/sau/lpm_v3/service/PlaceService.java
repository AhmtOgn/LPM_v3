package sau.lpm_v3.service;

import sau.lpm_v3.dtos.PlaceDTO;
import sau.lpm_v3.model.Place;

import java.util.List;

public interface PlaceService {
    public List<PlaceDTO> getAllPlaces();
    public PlaceDTO getPlaceById(Long id);
    public Place getPlaceEntityById(Long id);
    public PlaceDTO createPlace(Place place);
    public PlaceDTO updatePlace(Long id, Place place);
    public void deletePlace(Long id);
}