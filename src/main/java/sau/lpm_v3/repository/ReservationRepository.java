package sau.lpm_v3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sau.lpm_v3.model.Reservation;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStudentUsername(String username);


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.place.id = :placeId " +
            "AND r.id <> :currentId " +
            "AND r.isCancelled = false " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime")
    boolean isPlaceOccupiedExcludingSelf(@Param("placeId") Long placeId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("currentId") Long currentId);


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.place.id = :placeId " +
            "AND r.isCancelled = false " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime")
    boolean isPlaceOccupied(@Param("placeId") Long placeId,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.student.id = :studentId " +
            "AND r.isCancelled = false " +
            "AND CAST(r.startTime AS date) = :date")
    boolean hasUserReachedDailyLimit(@Param("studentId") Long studentId,
                                     @Param("date") LocalDate date);


}