package sau.lpm_v3.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private long id;
    private LocalDateTime date;
    private LocalDateTime duration;
    @JsonProperty("reserved")
    private boolean isReserved;

    private StudentDTO studentDTO;
    private PlaceDTO placeDTO;

    public ReservationDTO(long id, LocalDateTime date, LocalDateTime duration, boolean isReserved) {
        this.id = id;
        this.date = date;
        this.duration = duration;
        this.isReserved = isReserved;
    }
}