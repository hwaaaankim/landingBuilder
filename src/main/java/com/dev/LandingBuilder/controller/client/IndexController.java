package com.dev.LandingBuilder.controller.client;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.service.client.CompanyService;
import com.dev.LandingBuilder.service.client.FormFieldService;
import com.dev.LandingBuilder.service.client.InquiryService;
import com.dev.LandingBuilder.service.client.SelectOptionService;

@Controller
public class IndexController {

	@Autowired
    private CompanyService companyService;

    @Autowired
    private FormFieldService formFieldService;

    @Autowired
    private SelectOptionService selectOptionService;
	
    @Autowired
    private InquiryService inquiryService;
	
	@GetMapping({"/", "", "/index"})
	public String index() {
		
		return "front/index";
	}
	
	@GetMapping("/{slugUrl}")
	public String slugUrl(@PathVariable("slugUrl") String slugUrl, Model model) {
		
		
		
	    // 1. 회사 정보 조회
//	    Company company = companyService.getCompanyByUrlSlug(slugUrl)
//	            .orElseThrow(() -> new RuntimeException("Company not found with URL slug: " + slugUrl));
//	    System.out.println("=== 회사 정보 ===");
//	    System.out.println("회사 이름: " + company.getName());
//	    System.out.println("회사 URL 슬러그: " + company.getUrlSlug());
//	    System.out.println("================");
		Optional<Company> optionalCompany = companyService.getCompanyByUrlSlug(slugUrl);

	    if (optionalCompany.isEmpty()) {
	        // 회사 정보가 없으면 "front/index" 반환
	        return "front/index";
	    }
	    // 2. 폼 필드 조회
	    var formFields = formFieldService.getFormFieldsByCompanyId(optionalCompany.get().getId());

	    // 3. 각 폼 필드의 SelectOption 조회
	    var formFieldsWithOptions = formFields.stream().map(formField -> {
	        var options = selectOptionService.getSelectOptionsByFormFieldId(formField.getId());
	        formField.setSelectOptions(options);
	        return formField;
	    }).toList();

	    // 디버깅 출력
//	    System.out.println("=== 폼 필드 정보 ===");
//	    for (FormField formField : formFieldsWithOptions) {
//	        System.out.println("폼 타입: " + formField.getFieldType());
//	        System.out.println("폼의 질문 문구: " + formField.getFieldName());
//	        if (formField.getPlaceholder() != null) {
//	            System.out.println("폼의 플레이스 홀더: " + formField.getPlaceholder());
//	        }
//	        if (formField.getFieldType() == FieldType.SELECT) {
//	            System.out.println("폼 타입: SELECT");
//	            int optionNumber = 1;
//	            for (SelectOption option : formField.getSelectOptions()) {
//	                System.out.println("셀렉트 옵션 " + optionNumber + ": " + option.getOptionValue());
//	                optionNumber++;
//	            }
//	        }
//	        System.out.println("--------------------");
//	    }

	    // 4. 모델에 데이터 추가
	    model.addAttribute("company", optionalCompany.get());
	    model.addAttribute("formFields", formFieldsWithOptions);

	    // 5. "front/conference"로 반환
	    return "front/conference";
	}
	
	@PostMapping("/inquiryInsert")
	public String insertInquiry(@RequestParam Map<String, String> formData) {
	    try {
	        inquiryService.saveInquiry(formData);
	        return "front/index";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "front/index";
	    }
	}

}
