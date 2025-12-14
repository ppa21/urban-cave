package com.urbancave.service;

import com.urbancave.domain.Appointment;
import com.urbancave.domain.Status;
import com.urbancave.dto.BookingRequest;
import com.urbancave.dto.BookingResponse;
import com.urbancave.repository.AppointmentRepository;
import com.urbancave.repository.ServiceRepository;
import com.urbancave.repository.ShiftRepository;
import com.urbancave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final AppointmentRepository appointmentRepository;
    private final ShiftRepository shiftRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // finds all available time slots for a stylist on a specific date for a given service
    // walks through the shift in 30 minute intervals
    public List<LocalTime> getAvailableSlots(Long stylistId, Long serviceId, LocalDate date) {
        log.info("Fetching available slots for stylist: {}, service: {}, date: {}", stylistId, serviceId, date);
        var service = serviceRepository.findById(serviceId).orElseThrow();
        int serviceDuration = service.getDuration(); // in minutes

        // checks to see if the stylist works on a specific date
        var stylistShift = shiftRepository.findByStylistIdAndDate(stylistId, date).orElse(null);
        if (stylistShift == null) {
            log.warn("No shift found for stylist {} on date {}", stylistId, date);
            return List.of(); // if stylist is not working, return an empty list
        }

        var appointments = appointmentRepository.findByStylistAndDate(stylistId, date); // existing appointments
        List<LocalTime> freeSlots = new ArrayList<>();
        LocalTime currentSlotStart = stylistShift.getStartTime(); // instantiated to when stylist starts working and then incremented by 30 mins

        // Walk through shift in 30-minute intervals
        // Loop continues while a full appointment can fit before (or exactly at) shift end
        while (currentSlotStart.plusMinutes(serviceDuration).isBefore(stylistShift.getEndTime()) || currentSlotStart.plusMinutes(serviceDuration).equals(stylistShift.getEndTime())) {
            LocalTime slotStart = currentSlotStart;
            LocalTime slotEnd = currentSlotStart.plusMinutes(serviceDuration);
            boolean conflict = false;

            // check if this potential slot overlaps with any existing bookings
            for (Appointment appointment : appointments) {
                LocalTime bookStart = appointment.getStartTime().toLocalTime();
                LocalTime bookEnd = appointment.getEndTime().toLocalTime();

                // conflict
                // slotStart < bookEnd && slotEnd > bookStart
                if (slotStart.isBefore(bookEnd) && slotEnd.isAfter(bookStart)) {
                    conflict = true;
                    break;
                }
            }

            if (!conflict) {
                freeSlots.add(slotStart);
            }

            // increment by 30 mins since no conflict
            currentSlotStart = currentSlotStart.plusMinutes(30);
        }

        return freeSlots;
    }

    // create new appointment and send confirmation email
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        var stylist = userRepository.findById(request.stylistId()).orElseThrow();
        var service = serviceRepository.findById(request.serviceId()).orElseThrow();

        // check if the stylist is even working that day
        var date = request.startTime().toLocalDate();
        if (shiftRepository.findByStylistIdAndDate(stylist.getId(), date).isEmpty()) {
            throw new IllegalArgumentException("Stylist " + stylist.getName() + " is not working on " + date);
        }

        var start = request.startTime();
        var end = start.plusMinutes(service.getDuration());

        // check if the slot is already booked
        // prevent double booking and race condition
        if (appointmentRepository.existsOverlap(stylist.getId(), start, end)) {
            throw new IllegalArgumentException("Slot is already booked by someone else. Please try a different time/date.");
        }

        var appointment = Appointment.builder()
                .clientName(request.clientName())
                .clientEmail(request.clientEmail())
                .stylist(stylist)
                .service(service)
                .startTime(start)
                .endTime(end)
                .status(Status.BOOKED)
                .build();

        appointmentRepository.save(appointment);
        notificationService.sendConfirmation(appointment);

        return new BookingResponse(appointment.getId(), "Booking successful");
    }
}
