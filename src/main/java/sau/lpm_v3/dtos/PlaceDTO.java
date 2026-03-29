package sau.lpm_v3.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sau.lpm_v3.model.Place;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private long id;
    private String building;
    private String floor;
    private String room;
    private long seat;

    public Place toEntity() {
        Place place = new Place();
        place.setId(this.id);
        place.setBuilding(this.building);
        place.setFloor(this.floor);
        place.setRoom(this.room);
        place.setSeat(this.seat);
        return place;
    }
}
