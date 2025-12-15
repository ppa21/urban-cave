package com.urbancave.config;

import com.urbancave.domain.Role;
import com.urbancave.domain.Service;
import com.urbancave.domain.Shift;
import com.urbancave.domain.User;
import com.urbancave.repository.AppointmentRepository;
import com.urbancave.repository.ServiceRepository;
import com.urbancave.repository.ShiftRepository;
import com.urbancave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final ServiceRepository serviceRepo;
    private final ShiftRepository shiftRepo;
    private final AppointmentRepository appointmentRepo;

    @Override
    public void run(String @NonNull ... args) {
        // --- WIPE AND RE-SEED DATA FOR DEVELOPMENT ---
        // Delete in reverse order of creation to respect foreign key constraints
        appointmentRepo.deleteAll();
        shiftRepo.deleteAll();
        userRepo.deleteAll();
        serviceRepo.deleteAll();

        // --- SEED SERVICES ---
        serviceRepo.save(new Service(null, "Men's Fade", new BigDecimal("35.00"), 45));
        serviceRepo.save(new Service(null, "Beard Trim", new BigDecimal("20.00"), 30));

        // --- SEED USERS AND SHIFTS ---
        User john = userRepo.save(User.builder().name("John Barber").email("john@uc.com").role(Role.STYLIST).build());

        // --- CUSTOM SCHEDULE GENERATION ---
        LocalDate today = LocalDate.now();

        // 1. Works Today
        createShift(john, today);

        // 2. Works Tomorrow
        createShift(john, today.plusDays(1));

        // 3. Skip Day 2 (Day Off!)

        // 4. Works Day 3, 4, 5
        createShift(john, today.plusDays(3));
        createShift(john, today.plusDays(4));
        createShift(john, today.plusDays(5));

        // 5. Works Day 6 and 7
        createShift(john, today.plusDays(6));
        createShift(john, today.plusDays(7));
    }

    private void createShift(User stylist, LocalDate date) {
        shiftRepo.save(Shift.builder()
                .stylist(stylist)
                .date(date)
                .startTime(LocalTime.of(9, 0)) // 9 AM
                .endTime(LocalTime.of(17, 0))  // 5 PM
                .build());
    }
}
