package com.dev.LandingBuilder.service.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.model.client.FormField;
import com.dev.LandingBuilder.model.client.Inquiry;
import com.dev.LandingBuilder.model.client.InquiryData;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.repository.FormFieldRepository;
import com.dev.LandingBuilder.repository.InquiryDataRepository;
import com.dev.LandingBuilder.repository.InquiryRepository;

import jakarta.transaction.Transactional;

@Service
public class InquiryService {
	
	@Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryDataRepository inquiryDataRepository;

    @Autowired
    private FormFieldRepository formFieldRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Inquiry createInquiry(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id " + companyId));
        Inquiry inquiry = new Inquiry();
        inquiry.setCompany(company);
        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getInquiriesByCompanyId(Long companyId) {
        return inquiryRepository.findAllByCompany_Id(companyId);
    }

    public Optional<Inquiry> getInquiryById(Long id) {
        return inquiryRepository.findById(id);
    }

    public void deleteInquiry(Long id) {
        inquiryRepository.deleteById(id);
    }
    
    @Transactional
    public void saveInquiry(Map<String, String> formData) {
        if (formData.isEmpty()) {
            throw new IllegalArgumentException("Form data cannot be empty.");
        }

        // 1. Company 조회
        Long companyId = Long.valueOf(formData.get("companyId"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

        // 2. Inquiry 생성 및 저장
        Inquiry inquiry = new Inquiry();
        inquiry.setCompany(company);
        inquiry = inquiryRepository.save(inquiry); // 저장 후 반환된 객체 사용

        // 3. InquiryData 생성 및 저장
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Company ID 필드는 무시
            if (!key.equals("companyId")) {
                Long formFieldId = Long.parseLong(key);
                
                // FormField 조회
                FormField formField = formFieldRepository.findById(formFieldId)
                        .orElseThrow(() -> new RuntimeException("FormField not found with ID: " + formFieldId));
                System.out.println(formField.getId());
                System.out.println(inquiry.getId());
                // InquiryData 생성
                InquiryData inquiryData = new InquiryData();
                inquiryData.setInquiry(inquiry); // 저장된 Inquiry 사용
                inquiryData.setFormField(formField);
                inquiryData.setFieldValue(value);

                // InquiryData 저장
                inquiryDataRepository.save(inquiryData);
            }
        }
    }
}