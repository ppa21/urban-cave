package com.urbancave.repository;

import com.urbancave.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findByStylistIdAndDate(Long stylistId, LocalDate date);
}
