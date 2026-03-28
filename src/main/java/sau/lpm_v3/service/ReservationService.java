package sau.lpm_v3.service;

import sau.lpm_v3.model.Reservation;

import java.util.List;

public interface ReservationService {
    public List<Reservation> getAllReservations();
    public Reservation getReservationById(Long id);
    public Reservation createReservation(Reservation reservation);
    public Reservation updateReservation(Long id, Reservation reservation);
    public void deleteReservation(Long id);
}