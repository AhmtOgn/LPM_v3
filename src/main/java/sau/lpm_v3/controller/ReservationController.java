package sau.lpm_v3.controller;

import sau.lpm_v3.dtos.ReservationDTO;
import sau.lpm_v3.model.Reservation;
import sau.lpm_v3.service.PlaceService;
import sau.lpm_v3.service.ReservationService;
import sau.lpm_v3.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;
    private final StudentService studentService;
    private final PlaceService placeService;

    public ReservationController(ReservationService reservationService, StudentService studentService, PlaceService placeService) {
        this.reservationService = reservationService;
        this.studentService = studentService;
        this.placeService = placeService;
    }

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<ReservationDTO>> getReservations() {
        logger.info("Gel all reservation");
        List<Reservation> reservations = reservationService.getAllReservations();
        List<ReservationDTO> dtos = reservations.stream()
                .map(Reservation::viewAsReservationDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/get/{id}", produces = "application/json")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        if (id == null || id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        logger.info("Get reservation by id {}", id);
        Reservation reservation = reservationService.getReservationById(id);
        return new ResponseEntity<>(reservation.viewAsReservationDTO(), HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        logger.info("Adding reservation {}", reservation);
        return new ResponseEntity<>(reservationService.createReservation(reservation), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long id, @RequestBody ReservationDTO dto) {
        if (id == null || id == 0 || dto == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        logger.info("Updating reservation {}", id);

        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setDate(dto.getDate());
        reservation.setDuration(dto.getDuration());
        reservation.setReserved(dto.isReserved());

        if (dto.getPlaceDTO() != null) {
            reservation.setPlace(placeService.getPlaceEntityById(dto.getPlaceDTO().getId()));
        }
        if (dto.getStudentDTO() != null) {
            reservation.setStudent(studentService.getStudentEntityById(dto.getStudentDTO().getId()));
        }

        Reservation updated = reservationService.updateReservation(id, reservation);
        return new ResponseEntity<>(updated.viewAsReservationDTO(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Reservation> deleteReservation(@PathVariable Long id) {
        if (id == null || id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        reservationService.deleteReservation(id);
        logger.info("Deleting reservation {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}