package sau.lpm_v3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import sau.lpm_v3.dtos.ReservationDTO;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAdmin && !res.getStudent().getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return res.viewAsReservationDTO();
    }

    @Override
    public List<ReservationDTO> getAllReservations(boolean isAdmin, String username) {
        List<Reservation> reservations;
        if (isAdmin) {
            reservations = reservationRepository.findAll();
        } else {
            reservations = reservationRepository.findByStudentUsername(username);
        }

        return reservations.stream()
                .map(Reservation::viewAsReservationDTO)
                .sorted((r1, r2) -> r2.getStartTime().compareTo(r1.getStartTime())) // Yeniden eskiye sırala
                .toList();
    }

    @Override
    public ReservationDTO createReservation(ReservationDTO dto, Authentication auth) {
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 1. Rol Kontrolü: User sadece kendi adına yapabilir
        if (!isAdmin) {
            dto.setStudentId(studentRepository.findByUsername(currentUsername).getId());
        }

        // 2. Zaman Geçerliliği: Geçmişe rezervasyon yapılamaz
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçmiş bir zamana rezervasyon yapılamaz.");
        }

        // 3. Tek Gün Kontrolü: Başlangıç ve bitiş aynı gün olmalı
        if (!dto.getStartTime().toLocalDate().equals(dto.getEndTime().toLocalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rezervasyon aynı gün içerisinde başlayıp bitmelidir.");
        }

        // 4. Günlük Limit Kontrolü
        if (reservationRepository.hasUserReachedDailyLimit(dto.getStudentId(), dto.getStartTime().toLocalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Günde sadece 1 rezervasyon hakkınız bulunmaktadır.");
        }

        // 5. Çakışma Kontrolü
        if (reservationRepository.isPlaceOccupied(dto.getPlaceId(), dto.getStartTime(), dto.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seçilen koltuk bu saatler arasında doludur.");
        }

        Reservation reservation = dto.toEntity();
        reservation.setStudent(studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Öğrenci bulunamadı")));

        reservation.setPlace(placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mekan bulunamadı")));
        reservation.setCancelled(false);

        log.info("RESERVATION: User [{}] reserved Place [{}]", currentUsername, dto.getPlaceId());
        return reservationRepository.save(reservation).viewAsReservationDTO();
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO dto, boolean isAdmin, String currentUsername) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezervasyon bulunamadı."));

        // Yetki: Admin değilse ve sahibi değilse reddet
        if (!isAdmin && !existing.getStudent().getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu rezervasyonu düzenleyemezsiniz.");
        }

        // Geçmiş rezervasyon düzenlenemez
        if (existing.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçmiş rezervasyonlar değiştirilemez.");
        }

        // Çakışma Kontrolü (Kendi ID'si hariç)
        boolean isOccupied = reservationRepository.isPlaceOccupiedExcludingSelf(
                dto.getPlaceId(), dto.getStartTime(), dto.getEndTime(), id);

        if (isOccupied) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seçilen koltuk bu saatlerde başka bir rezervasyonla çakışıyor.");
        }

        existing.setPlace(placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mekan bulunamadı")));
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        log.info("RESERVATION UPDATED: ID [{}] by user [{}]", id, currentUsername);
        return reservationRepository.save(existing).viewAsReservationDTO();
    }

    @Override
    public void cancelReservation(Long id, boolean isAdmin, String currentUsername) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAdmin && !res.getStudent().getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (res.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçmiş rezervasyonlar üzerinde işlem yapılamaz.");
        }

        res.setCancelled(true);
        reservationRepository.save(res);
        log.warn("CANCELLED: Reservation [{}] by [{}]", id, currentUsername);
    }
}