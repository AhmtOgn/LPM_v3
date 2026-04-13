package sau.lpm_v3.service;

import sau.lpm_v3.dtos.ReservationDTO;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface ReservationService {
    ReservationDTO createReservation(ReservationDTO dto, Authentication auth);
    List<ReservationDTO> getAllReservations(boolean isAdmin, String username);
    public ReservationDTO updateReservation(Long id, ReservationDTO dto, boolean isAdmin, String currentUsername);
    void cancelReservation(Long id, boolean isAdmin, String currentUsername);
    ReservationDTO getReservationById(Long id, boolean isAdmin, String currentUsername);
}