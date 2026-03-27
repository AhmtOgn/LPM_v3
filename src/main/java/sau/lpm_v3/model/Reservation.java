package sau.lpm_v3.model;

import sau.lpm_v3.dtos.ReservationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    
    
    public ReservationDTO viewAsReservationDTO() {
        ReservationDTO dto = new ReservationDTO(id, date, duration, isReserved);
        if (student != null) {
            dto.setStudentDTO(student.viewAsStudentDTO());
        }
        if (place != null) {
            dto.setPlaceDTO(place.viewAsPlaceDTO());
        }
        return dto;
    }

    public Reservation(Reservation reservationDTO) {
        this.id = reservationDTO.getId();
        this.date = reservationDTO.getDate();
        this.duration = reservationDTO.getDuration();
        this.isReserved = reservationDTO.isReserved();
        this.student = null;
        this.place = null;
    }
    

    
}