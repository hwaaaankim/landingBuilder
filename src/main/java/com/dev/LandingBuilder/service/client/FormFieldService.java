package com.dev.LandingBuilder.service.client;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.LandingBuilder.dto.FormFieldDTO;
import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.model.client.FieldType;
import com.dev.LandingBuilder.model.client.FormField;
import com.dev.LandingBuilder.repository.FormFieldRepository;

import jakarta.transaction.Transactional;

@Service
public class FormFieldService {
	
	private final FormFieldRepository formFieldRepository;
    private final SelectOptionService selectOptionService;

    public FormFieldService(FormFieldRepository formFieldRepository, SelectOptionService selectOptionService) {
        this.formFieldRepository = formFieldRepository;
        this.selectOptionService = selectOptionService;
    }

    @Transactional
    public void saveFormFields(Company company, List<FormFieldDTO> formFields) {
        for (FormFieldDTO fieldDTO : formFields) {
            // FormField 엔티티 생성 및 저장
            FormField formField = new FormField();
            formField.setCompany(company);
            formField.setFieldName(fieldDTO.getFieldName());
            formField.setPlaceholder(fieldDTO.getPlaceholder());
            formField.setFieldType(fieldDTO.getFieldType());
            formField.setIsRequired(fieldDTO.getIsRequired());

            // formField 저장 후 ID가 설정된 객체 반환
            FormField savedFormField = formFieldRepository.saveAndFlush(formField);

            // SELECT 타입의 필드에 대해 옵션 저장
            if (fieldDTO.getFieldType() == FieldType.SELECT && fieldDTO.getOptions() != null) {
                // 외래 키 참조를 위해 저장된 savedFormField 객체를 사용
                selectOptionService.saveSelectOptions(savedFormField, fieldDTO.getOptions());
            }
        }
    }
    
    public List<FormField> getFormFieldsByCompanyId(Long companyId) {
        return formFieldRepository.findByCompany_Id(companyId);
    }
}

