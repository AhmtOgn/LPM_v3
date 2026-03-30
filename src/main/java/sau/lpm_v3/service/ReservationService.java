package sau.lpm_v3.service;

import sau.lpm_v3.dtos.ReservationDTO;
import sau.lpm_v3.model.Reservation;

import java.util.List;

public interface ReservationService {
    public List<ReservationDTO> getAllReservations();
    public ReservationDTO getReservationById(Long id);
    public ReservationDTO createReservation(ReservationDTO reservationDto);
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDto);
    public void deleteReservation(Long id);
}