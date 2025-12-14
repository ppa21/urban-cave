package com.urbancave.contoller;

import com.urbancave.domain.Service;
import com.urbancave.domain.User;
import com.urbancave.dto.BookingRequest;
import com.urbancave.dto.BookingResponse;
import com.urbancave.service.BookingService;
import com.urbancave.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ShopController {
    private final BookingService bookingService;
    private final ShopService shopService;

    @GetMapping("/services")
    public List<Service> getServices() {
        return shopService.getServices();
    }

    @GetMapping("/stylists")
    public List<User> getStylists() {
        return shopService.getStylists();
    }

    // dynamic slots
    @GetMapping("/slots")
    public List<LocalTime> getSlots(
            @RequestParam Long stylistId,
            @RequestParam Long serviceId,
            @RequestParam LocalDate date) {
        return bookingService.getAvailableSlots(stylistId, serviceId, date);
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookService(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }
}
