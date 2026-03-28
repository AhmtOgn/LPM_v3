package sau.lpm_v3.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private long id;
    private String building;
    private String floor;
    private String room;
    private long seat;
}
