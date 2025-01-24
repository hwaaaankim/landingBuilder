package com.dev.LandingBuilder.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dev.LandingBuilder.dto.FormFieldDTO;
import com.dev.LandingBuilder.dto.InquiryRequestDTO;
import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.service.client.CompanyService;
import com.dev.LandingBuilder.service.client.FormFieldService;

@Controller
public class AdminController {

	@Autowired
    private CompanyService companyService;
	
	@Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FormFieldService formFieldService;
	
	@GetMapping("/insertClientForm")
	public String insertClientForm() {
		
		return "administration/insertClientForm";
	}
	
	@PostMapping("/insertClient")
	@ResponseBody
    public String insertClient(
            @RequestParam("name") String name,
            @RequestParam("representativeName") String representativeName,
            @RequestParam("businessRegistrationNumber") String businessRegistrationNumber,
            @RequestParam("address") String address,
            @RequestParam("conferenceAddress") String conferenceAddress,
            @RequestParam("logoImage") MultipartFile logoImage,
            @RequestParam("urlSlug") String urlSlug,
            @RequestParam("mainImage") MultipartFile mainImage,
            @RequestParam("homepageUrl") String homepageUrl,
            @RequestParam("snsUrl") String snsUrl,
            @RequestParam("mapUrl") String mapUrl,
            @RequestParam("slideImages") List<MultipartFile> slideImages
    ) throws IOException {
        companyService.createCompany(
                name,
                representativeName,
                businessRegistrationNumber,
                address,
                conferenceAddress,
                logoImage,
                urlSlug,
                mainImage,
                homepageUrl,
                snsUrl,
                mapUrl,
                slideImages
        );
        return "Company successfully registered.";
    }
	
	@GetMapping("/inquiryCustom/{slugUrl}")
	public String getInquiryCustomPage(@PathVariable String slugUrl, Model model) {
	    Optional<Company> companyOptional = companyService.getCompanyByUrlSlug(slugUrl);
	    if (companyOptional.isPresent()) {
	        Company company = companyOptional.get();
	        model.addAttribute("company", company);
	        return "administration/inquiryCustom";
	    } else {
	        throw new RuntimeException("Company not found for slugUrl: " + slugUrl);
	    }
	}
	
	@PostMapping("/inquiryCustomSubmit")
	public ResponseEntity<String> submitCustomFormFields(
	        @RequestBody InquiryRequestDTO inquiryRequestDTO
	) {
	    String urlSlug = inquiryRequestDTO.getUrlSlug();
	    List<FormFieldDTO> formFields = inquiryRequestDTO.getFormFields();

	    System.out.println("urlSlug: " + urlSlug);
	    System.out.println("formFields: " + formFields);

	    // Find the company by slugUrl
	    Company company = companyRepository.findByUrlSlug(urlSlug)
	            .orElseThrow(() -> new RuntimeException("Company not found for slugUrl: " + urlSlug));

	    // Save form fields
	    formFieldService.saveFormFields(company, formFields);

	    return ResponseEntity.ok("Form fields saved successfully!");
	}
}

