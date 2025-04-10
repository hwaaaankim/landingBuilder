package com.dev.LandingBuilder.model.client;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TB_COMPANY")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "representative_name", nullable = false)
    private String representativeName;

    @Column(name = "business_registration_number", nullable = false)
    private String businessRegistrationNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "conference_address")
    private String conferenceAddress;

    @Column(name = "logo_image_path")
    private String logoImagePath; // 실제 저장 경로

    @Column(name = "logo_image_road")
    private String logoImageRoad; // 로드 경로

    @Column(name = "url_slug", unique = true, nullable = false)
    private String urlSlug;

    @Column(name = "main_image_path")
    private String mainImagePath; // 실제 저장 경로

    @Column(name = "main_image_road")
    private String mainImageRoad; // 로드 경로
    
    @Column(name = "left_image_path")
    private String leftImagePath; // 실제 저장 경로

    @Column(name = "left_image_road")
    private String leftImageRoad; // 로드 경로
    
    @Column(name = "right_image_path")
    private String rightImagePath; // 실제 저장 경로

    @Column(name = "right_image_road")
    private String rightImageRoad; // 로드 경로

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "sns_url")
    private String snsUrl;

    @Column(name = "map_url")
    private String mapUrl;
    
    @Column(name = "script_one", columnDefinition = "TEXT")
    private String scriptOne;

    @Column(name = "script_two", columnDefinition = "TEXT")
    private String scriptTwo;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompanySlideImage> slideImages;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormField> formFields;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquiry> inquiries;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
