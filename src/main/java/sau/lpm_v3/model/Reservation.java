package sau.lpm_v3.model;

import jakarta.persistence.*;
import lombok.*;
import sau.lpm_v3.dtos.ReservationDTO;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean isCancelled = false;

    public ReservationDTO viewAsReservationDTO() {
        String builtPlaceName = (place != null)
                ? place.getBuilding() + " - " + place.getRoom()
                : null;
        return ReservationDTO.builder()
                .id(this.id)
                .studentId(this.student != null ? this.student.getId() : null)
                .studentName(this.student != null ? this.student.getName() : null)
                .studentUsername(this.student != null ? this.student.getUsername() : null)
                .placeId(this.place != null ? this.place.getId() : null)
                .placeName(builtPlaceName)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .isCancelled(this.isCancelled)
                .build();
    }
}