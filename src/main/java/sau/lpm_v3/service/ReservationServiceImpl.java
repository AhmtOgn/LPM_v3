package sau.lpm_v3.service;

import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.Reservation;
import sau.lpm_v3.repository.PlaceRepository;
import sau.lpm_v3.repository.ReservationRepository;
import sau.lpm_v3.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final StudentRepository studentRepository;
    private final PlaceRepository placeRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, StudentRepository studentRepository, PlaceRepository placeRepository) {
        this.reservationRepository = reservationRepository;
        this.studentRepository = studentRepository;
        this.placeRepository = placeRepository;
    }
    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    @Override
    public Reservation createReservation(Reservation reservation) {
        if (reservationRepository.findById(reservation.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_PLACE_ALREADY_EXIST + ": " + reservation.getId());
        }
        return reservationRepository.save(reservation);
    }
    @Override
    public Reservation updateReservation(Long id, Reservation reservation) {
        reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
        reservation.setId(id);
        return reservationRepository.save(reservation);
    }
    @Override
    public void deleteReservation(Long id) {
        reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_RESERVATION_NOT_FOUND + ": " + id));
        reservationRepository.deleteById(id);
    }
}