package sau.lpm_v3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import sau.lpm_v3.dtos.ReservationDTO;
import sau.lpm_v3.model.*;
import sau.lpm_v3.repository.PlaceRepository;
import sau.lpm_v3.repository.ReservationRepository;
import sau.lpm_v3.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final StudentRepository studentRepository;
    private final PlaceRepository placeRepository;

    @Override
    public ReservationDTO getReservationById(Long id, boolean isAdmin, String currentUsername) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("RESERVATION NOT FOUND: id=[{}], requestedBy=[{}]", id, currentUsername);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Reservation not found: " + id);
                });

        if (!isAdmin && !res.getStudent().getUsername().equals(currentUsername)) {
            log.warn("RESERVATION ACCESS DENIED: id=[{}], owner=[{}], requestedBy=[{}]",
                    id, res.getStudent().getUsername(), currentUsername);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to view this reservation.");
        }
        return res.viewAsReservationDTO();
    }

    @Override
    public List<ReservationDTO> getAllReservations(boolean isAdmin, String username) {
        List<Reservation> reservations = isAdmin
                ? reservationRepository.findAll()
                : reservationRepository.findByStudentUsername(username);

        log.info("RESERVATION LIST: fetched={}, by=[{}], scope=[{}]",
                reservations.size(), username, isAdmin ? "ALL" : "OWN");

        return reservations.stream()
                .map(Reservation::viewAsReservationDTO)
                .sorted((r1, r2) -> r2.getStartTime().compareTo(r1.getStartTime()))
                .toList();
    }

    @Override
    public ReservationDTO createReservation(ReservationDTO dto, Authentication auth) {
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            dto.setStudentId(studentRepository.findByUsername(currentUsername).getId());
        }

        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            log.warn("RESERVATION CREATE REJECTED (past time): user=[{}], start=[{}]",
                    currentUsername, dto.getStartTime());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Reservations cannot be made for a past date.");
        }

        if (!dto.getStartTime().toLocalDate().equals(dto.getEndTime().toLocalDate())) {
            log.warn("RESERVATION CREATE REJECTED (multi-day): user=[{}]", currentUsername);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Reservations must start and end on the same day.");
        }

        if (reservationRepository.hasUserReachedDailyLimit(
                dto.getStudentId(), dto.getStartTime().toLocalDate())) {
            log.warn("RESERVATION CREATE REJECTED (daily limit): user=[{}], date=[{}]",
                    currentUsername, dto.getStartTime().toLocalDate());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You are only allowed one reservation per day.");
        }

        if (reservationRepository.isPlaceOccupied(
                dto.getPlaceId(), dto.getStartTime(), dto.getEndTime())) {
            log.warn("RESERVATION CREATE REJECTED (conflict): user=[{}], place=[{}], start=[{}], end=[{}]",
                    currentUsername, dto.getPlaceId(), dto.getStartTime(), dto.getEndTime());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The selected seat is fully booked during these hours.");
        }

        Reservation reservation = dto.toEntity();
        reservation.setStudent(studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")));
        reservation.setPlace(placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found.")));
        reservation.setCancelled(false);

        ReservationDTO saved = reservationRepository.save(reservation).viewAsReservationDTO();

        log.info("RESERVATION CREATED: id=[{}], createdBy=[{}], role=[{}], placeId=[{}], start=[{}], end=[{}]",
                saved.getId(), currentUsername, isAdmin ? "ADMIN" : "USER",
                dto.getPlaceId(), dto.getStartTime(), dto.getEndTime());

        return saved;
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO dto,
                                            boolean isAdmin, String currentUsername) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("RESERVATION UPDATE FAILED (not found): id=[{}], by=[{}]", id, currentUsername);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found.");
                });

        if (!isAdmin && !existing.getStudent().getUsername().equals(currentUsername)) {
            log.warn("RESERVATION UPDATE DENIED: id=[{}], owner=[{}], attemptedBy=[{}]",
                    id, existing.getStudent().getUsername(), currentUsername);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bu rezervasyonu düzenleyemezsiniz.");
        }

        if (existing.getEndTime().isBefore(LocalDateTime.now())) {
            log.warn("RESERVATION UPDATE REJECTED (past): id=[{}], by=[{}]", id, currentUsername);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Past reservations cannot be changed.");
        }

        if (reservationRepository.isPlaceOccupiedExcludingSelf(
                dto.getPlaceId(), dto.getStartTime(), dto.getEndTime(), id)) {
            log.warn("RESERVATION UPDATE REJECTED (conflict): id=[{}], place=[{}], by=[{}]",
                    id, dto.getPlaceId(), currentUsername);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The selected seat clashes with another reservation at this time.");
        }

        existing.setPlace(placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found")));
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        ReservationDTO updated = reservationRepository.save(existing).viewAsReservationDTO();

        log.info("RESERVATION UPDATED: id=[{}], updatedBy=[{}], role=[{}], newPlace=[{}], start=[{}], end=[{}]",
                id, currentUsername, isAdmin ? "ADMIN" : "USER",
                dto.getPlaceId(), dto.getStartTime(), dto.getEndTime());

        return updated;
    }

    @Override
    public void cancelReservation(Long id, boolean isAdmin, String currentUsername) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("RESERVATION CANCEL FAILED (not found): id=[{}], by=[{}]", id, currentUsername);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Reservation not found: " + id);
                });

        if (!isAdmin && !res.getStudent().getUsername().equals(currentUsername)) {
            log.warn("RESERVATION CANCEL DENIED: id=[{}], owner=[{}], attemptedBy=[{}]",
                    id, res.getStudent().getUsername(), currentUsername);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You cannot cancel this reservation.");
        }

        if (res.getStartTime().isBefore(LocalDateTime.now())) {
            log.warn("RESERVATION CANCEL REJECTED (past): id=[{}], by=[{}]", id, currentUsername);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Past reservations cannot be changed.");
        }

        res.setCancelled(true);
        reservationRepository.save(res);

        log.info("RESERVATION CANCELLED: id=[{}], cancelledBy=[{}], role=[{}], place=[{}]",
                id, currentUsername, isAdmin ? "ADMIN" : "USER",
                res.getPlace().getId());
    }
}