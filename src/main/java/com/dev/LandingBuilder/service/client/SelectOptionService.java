package com.dev.LandingBuilder.service.client;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.LandingBuilder.model.client.FormField;
import com.dev.LandingBuilder.model.client.SelectOption;
import com.dev.LandingBuilder.repository.SelectOptionRepository;

import jakarta.transaction.Transactional;


@Service
public class SelectOptionService {
	
	private final SelectOptionRepository selectOptionRepository;

    public SelectOptionService(SelectOptionRepository selectOptionRepository) {
        this.selectOptionRepository = selectOptionRepository;
    }

    @Transactional
    public void saveSelectOptions(FormField formField, List<String> options) {
        int displayOrder = 1;
        System.out.println(formField.getId());
        for (String optionValue : options) {
            SelectOption selectOption = new SelectOption();
            selectOption.setFormField(formField);
            selectOption.setOptionValue(optionValue);
            selectOption.setDisplayOrder(displayOrder++);
            selectOptionRepository.save(selectOption);
        }
    }
    
    public List<SelectOption> getSelectOptionsByFormFieldId(Long formFieldId) {
        return selectOptionRepository.findByFormField_Id(formFieldId);
    }
}
