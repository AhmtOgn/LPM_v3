package sau.lpm_v3.service;

import sau.lpm_v3.dtos.ReservationDTO;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.*;
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
    public ReservationDTO getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_RESERVATION_NOT_FOUND + ": " + id)).viewAsReservationDTO();
    }

    @Override
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream().map(Reservation::viewAsReservationDTO).toList();
    }

    @Override
    public ReservationDTO createReservation(ReservationDTO reservationDto) {
        // Convert DTO to Entity internally
        Reservation reservation = reservationDto.toEntity();

        // BUG FIX: Student ve Place entity'lerini repository'den çekip ilişkileri kur
        Student student = studentRepository.findById(reservationDto.getStudentDto().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found: " + reservationDto.getStudentDto().getId()));
        Place place = placeRepository.findById(reservationDto.getPlaceDto().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Place not found: " + reservationDto.getPlaceDto().getId()));

        reservation.setStudent(student);
        reservation.setPlace(place);

        return reservationRepository.save(reservation).viewAsReservationDTO();
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDto) {
        reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_RESERVATION_NOT_FOUND + ": " + id));

        Reservation reservation = reservationDto.toEntity();
        reservation.setId(id); // URL'den gelen güvenli ID

        Student student = studentRepository.findById(reservationDto.getStudentDto().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found: " + reservationDto.getStudentDto().getId()));
        Place place = placeRepository.findById(reservationDto.getPlaceDto().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Place not found: " + reservationDto.getPlaceDto().getId()));

        reservation.setStudent(student);
        reservation.setPlace(place);

        return reservationRepository.save(reservation).viewAsReservationDTO();
    }

    public void deleteReservation(Long id) {
        reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_RESERVATION_NOT_FOUND + ": " + id));
        reservationRepository.deleteById(id);
    }
}