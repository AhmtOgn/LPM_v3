package sau.lpm_v3.dtos;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import sau.lpm_v3.model.Reservation;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long id;
    private Long studentId;
    private Long placeId;

    private String studentName;
    private String studentUsername;
    private String placeName;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    private boolean isCancelled;

    public String getDynamicStatus() {
        if (isCancelled) return "Cancelled";

        LocalDateTime now = LocalDateTime.now();
        if (endTime.isBefore(now)) return "Past";
        if (startTime.isAfter(now)) return "Oncoming";

        return "Continues";
    }

    public Reservation toEntity() {
        Reservation res = new Reservation();
        res.setId(this.id);
        res.setStartTime(this.startTime);
        res.setEndTime(this.endTime);
        res.setCancelled(this.isCancelled);

        return res;
    }
}