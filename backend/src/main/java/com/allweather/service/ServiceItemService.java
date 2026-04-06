package com.allweather.service;

import com.allweather.model.ServiceItem;
import com.allweather.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceItemService {

    private final ServiceRepository serviceRepository;

    /**
     * Get all active services.
     */
    @Transactional(readOnly = true)
    public List<ServiceItem> getAllActiveServices() {
        return serviceRepository.findByIsActiveTrueOrderByIdAsc();
    }

    /**
     * Get all services (including inactive) for admin.
     */
    @Transactional(readOnly = true)
    public List<ServiceItem> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Get a single service by ID.
     */
    @Transactional(readOnly = true)
    public Optional<ServiceItem> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    /**
     * Create a new service.
     */
    public ServiceItem createService(ServiceItem service) {
        return serviceRepository.save(service);
    }

    /**
     * Update an existing service.
     */
    public Optional<ServiceItem> updateService(Long id, ServiceItem updatedService) {
        return serviceRepository.findById(id).map(existing -> {
            existing.setName(updatedService.getName());
            existing.setDescription(updatedService.getDescription());
            existing.setPrice(updatedService.getPrice());
            existing.setPriceUnit(updatedService.getPriceUnit());
            existing.setIcon(updatedService.getIcon());
            existing.setIsActive(updatedService.getIsActive());
            return serviceRepository.save(existing);
        });
    }
}
