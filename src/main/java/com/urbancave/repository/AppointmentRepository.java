package com.urbancave.repository;

import com.urbancave.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // check overlap
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.stylist.id = :stylistId AND a.status = 'BOOKED' " +
            "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean existsOverlap(@Param("stylistId") Long stylistId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    // get all bookings for a stylist for a particular date
    @Query("SELECT a FROM Appointment a WHERE a.stylist.id = :stylistId " +
            "AND CAST(a.startTime AS LocalDate) = :date " +
            "AND a.status = 'BOOKED'")
    List<Appointment> findByStylistAndDate(@Param("stylistId") Long stylistId,
                                           @Param("date") LocalDate date);
}
