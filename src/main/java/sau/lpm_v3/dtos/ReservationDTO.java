package sau.lpm_v3.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sau.lpm_v3.model.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservationDTO {
    private long id;
    private LocalDateTime date;
    private LocalDateTime duration;
    @JsonProperty("reserved")
    private boolean isReserved;

    private StudentDTO studentDto;
    private PlaceDTO placeDto;

    public ReservationDTO(long id, LocalDateTime date, LocalDateTime duration, boolean isReserved, StudentDTO studentDto, PlaceDTO placeDto) {
        this.id = id;
        this.date = date;
        this.duration = duration;
        this.isReserved = isReserved;
        this.studentDto = studentDto;
        this.placeDto = placeDto;
        // FK id'leri de doldur (update formunda seçili gelmesi için)
        // if (studentDto != null) this.studentId = studentDto.getId();
        // if (placeDto != null) this.placeId = placeDto.getId();
    }

    public Reservation toEntity() {
        Reservation reservation = new Reservation();
        reservation.setId(this.id);
        reservation.setDate(this.date);
        reservation.setDuration(this.duration);
        reservation.setReserved(this.isReserved);
        // Student ve Place → ReservationServiceImpl içinde repository'den çekilip set edilir
        return reservation;
    }
}