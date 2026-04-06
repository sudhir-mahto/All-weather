package com.allweather.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a service offered by All Weather Solution.
 * Named ServiceItem to avoid conflict with java.util.Service.
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Price in INR. 0 means "contact for quote".
     */
    @Column(nullable = false)
    @Builder.Default
    private Double price = 0.0;

    /**
     * Price unit context: "starting", "per year", "contact", "flat", etc.
     */
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String priceUnit = "starting";

    /**
     * Font Awesome icon class (e.g., "fa-snowflake").
     */
    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
