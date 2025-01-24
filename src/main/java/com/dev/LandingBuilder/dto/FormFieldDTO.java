package com.dev.LandingBuilder.dto;

import java.util.List;

import com.dev.LandingBuilder.model.client.FieldType;

import lombok.Data;

@Data
public class FormFieldDTO {
    private String fieldName;
    private String placeholder;
    private FieldType fieldType;
    private Boolean isRequired;
    private List<String> options; // Only for SELECT fields
}
