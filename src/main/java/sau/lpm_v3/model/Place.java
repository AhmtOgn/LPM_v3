package sau.lpm_v3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import sau.lpm_v3.dtos.PlaceDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Place {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 64)
    private String building;
    @Column(length = 8)
    private String floor;
    @Column(length = 16)
    private String room ;
    private long seat;
    

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> reservations;

    public Place(PlaceDTO placeDTO) {
        this.id = placeDTO.getId();
        this.building = placeDTO.getBuilding();
        this.floor = placeDTO.getFloor();
        this.room = placeDTO.getRoom();
        this.seat = placeDTO.getSeat();
    }
    public PlaceDTO viewAsPlaceDTO() {
        return new PlaceDTO(id, building, floor, room , seat);
    }
}