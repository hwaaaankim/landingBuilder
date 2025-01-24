package com.dev.LandingBuilder.dto;

import java.util.List;

import lombok.Data;

@Data
public class InquiryRequestDTO {
    private String urlSlug;
    private List<FormFieldDTO> formFields;
}