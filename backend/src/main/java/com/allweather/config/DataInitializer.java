package com.allweather.config;

import com.allweather.model.ServiceItem;
import com.allweather.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds initial service data on application startup.
 * Only inserts data if the services table is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (serviceRepository.count() == 0) {
            log.info("Seeding initial service data...");
            seedServices();
            log.info("Service data seeded successfully.");
        } else {
            log.info("Service data already exists, skipping seed.");
        }
    }

    private void seedServices() {
        serviceRepository.save(ServiceItem.builder()
            .name("Split AC Repair")
            .description("Gas Filling, PCB Repair, Cooling Problems, Compressor Issues – Full diagnosis and repair for all split AC brands including Voltas, LG, Samsung, Daikin, Hitachi, and more.")
            .price(500.0)
            .priceUnit("starting")
            .icon("fa-snowflake")
            .isActive(true)
            .build());

        serviceRepository.save(ServiceItem.builder()
            .name("Window AC Service")
            .description("Complete Cleaning, Repair & Maintenance for Window ACs – Quick & Affordable service. Includes filter cleaning, coil wash, and performance check.")
            .price(400.0)
            .priceUnit("starting")
            .icon("fa-wind")
            .isActive(true)
            .build());

        serviceRepository.save(ServiceItem.builder()
            .name("AC Installation")
            .description("New AC Fitting, Copper Pipe Installation, Testing & Setup – Same Day Service Available. We handle all brands and types of ACs professionally.")
            .price(0.0)
            .priceUnit("contact")
            .icon("fa-tools")
            .isActive(true)
            .build());

        serviceRepository.save(ServiceItem.builder()
            .name("Annual Maintenance Contract (AMC)")
            .description("4 Professional Services Per Year – Priority Response, Discounted Parts, and Peace of Mind. Cover your AC with our comprehensive AMC plan.")
            .price(2000.0)
            .priceUnit("per year")
            .icon("fa-calendar-check")
            .isActive(true)
            .build());

        serviceRepository.save(ServiceItem.builder()
            .name("AC Gas Refill")
            .description("R22, R32, R410A Gas Refilling – Restore your AC's cooling power. We use genuine refrigerant gas and perform leak testing after refilling.")
            .price(800.0)
            .priceUnit("starting")
            .icon("fa-thermometer-half")
            .isActive(true)
            .build());

        serviceRepository.save(ServiceItem.builder()
            .name("AC Deep Cleaning")
            .description("Complete Deep Cleaning of Indoor & Outdoor Unit – High-pressure foam wash, anti-bacterial treatment, and coil cleaning for maximum efficiency.")
            .price(600.0)
            .priceUnit("starting")
            .icon("fa-broom")
            .isActive(true)
            .build());

        log.info("Seeded 6 services.");
    }
}
