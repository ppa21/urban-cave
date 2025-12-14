package com.urbancave.service;

import com.urbancave.domain.Role;
import com.urbancave.domain.User;
import com.urbancave.repository.ServiceRepository;
import com.urbancave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public List<com.urbancave.domain.Service> getServices() {
        return serviceRepository.findAll();
    }

    public List<User> getStylists() {
        return userRepository.findByRole(Role.STYLIST);
    }
}
