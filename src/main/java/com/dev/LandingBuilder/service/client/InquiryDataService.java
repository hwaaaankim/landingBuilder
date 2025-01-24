package com.dev.LandingBuilder.service.client;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.LandingBuilder.model.client.FormField;
import com.dev.LandingBuilder.model.client.Inquiry;
import com.dev.LandingBuilder.model.client.InquiryData;
import com.dev.LandingBuilder.repository.FormFieldRepository;
import com.dev.LandingBuilder.repository.InquiryDataRepository;
import com.dev.LandingBuilder.repository.InquiryRepository;

@Service
public class InquiryDataService {
	
    @Autowired
    private InquiryDataRepository inquiryDataRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private FormFieldRepository formFieldRepository;

    public InquiryData createInquiryData(Long inquiryId, Long formFieldId, String fieldValue) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with id " + inquiryId));
        FormField formField = formFieldRepository.findById(formFieldId)
                .orElseThrow(() -> new RuntimeException("FormField not found with id " + formFieldId));
        InquiryData inquiryData = new InquiryData();
        inquiryData.setInquiry(inquiry);
        inquiryData.setFormField(formField);
        inquiryData.setFieldValue(fieldValue);
        return inquiryDataRepository.save(inquiryData);
    }

    public List<InquiryData> getInquiryDataByInquiryId(Long inquiryId) {
        return inquiryDataRepository.findAllByInquiry_Id(inquiryId);
    }

    public Optional<InquiryData> getInquiryDataById(Long id) {
        return inquiryDataRepository.findById(id);
    }

    public void deleteInquiryData(Long id) {
        inquiryDataRepository.deleteById(id);
    }
}
