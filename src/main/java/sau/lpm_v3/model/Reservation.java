package sau.lpm_v3.model;

import sau.lpm_v3.dtos.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sau.lpm_v3.dtos.StudentDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="date")
    private LocalDateTime date;
    @Column(name="duration")
    private LocalDateTime duration;
    @Column(name="is_reserved")
    private boolean isReserved = false;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
    
    public ReservationDTO viewAsReservationDTO() {
        StudentDTO studentDto = (student != null) ? student.viewAsStudentDTO() : null;
        PlaceDTO placeDto = (place != null) ? place.viewAsPlaceDTO() : null;

        return new ReservationDTO(id, date, duration, isReserved, studentDto, placeDto);
    }

    //<form th:action="@{/reservation/update/{id}(id=${reservation.id})}" th:field="${reservation}" method="post">
    //                    <input type="hidden" th:field="*{id}" />
}