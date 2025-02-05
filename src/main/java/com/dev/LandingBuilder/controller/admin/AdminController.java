package com.dev.LandingBuilder.controller.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dev.LandingBuilder.dto.FormFieldDTO;
import com.dev.LandingBuilder.dto.InquiryRequestDTO;
import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.model.client.Inquiry;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.repository.InquiryRepository;
import com.dev.LandingBuilder.service.client.CompanyService;
import com.dev.LandingBuilder.service.client.FormFieldService;
import com.dev.LandingBuilder.service.client.InquiryService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
    private CompanyService companyService;
	
	@Autowired
	private InquiryService inquiryService;
	
	@Autowired
    private CompanyRepository companyRepository;

	@Autowired
	private InquiryRepository inquiryRepository;
	
    @Autowired
    private FormFieldService formFieldService;

	@GetMapping({"/insertClientForm", "", "/"})
	public String insertClientForm() {
		
		return "administration/insertClientForm";
	}
	
	@GetMapping("/clientManager")
	public String clientManager(
			Model model,
			@PageableDefault(size=15) Pageable pageable
			) {
		
		Page<Inquiry> inquiries = inquiryRepository.findAllByOrderByIdDesc(pageable);
		
		int startPage = Math.max(1, inquiries.getPageable().getPageNumber() - 4);
		int endPage = Math.min(inquiries.getTotalPages(), inquiries.getPageable().getPageNumber() + 4);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("inquiries", inquiries);
		return "administration/clientManager";
	}
	
	@GetMapping("/clientDetail/{id}")
	public String clientDetail(
			Model model,
			@PathVariable Long id
			) {
		
		model.addAttribute("inquiry", inquiryRepository.findById(id).get());
		return "administration/clientDetail";
	}
	
	@PostMapping("/inquiryUpdate")
	public String inquiryUpdate(
			@RequestParam("id") Long id,
			@RequestParam("comment") String comment,
			@RequestParam("sign") Boolean sign,
			Model model
			) {
		inquiryService.updateInquiry(id, comment, sign);
		model.addAttribute("inquiry", inquiryRepository.findById(id).get());
		return "administration/clientDetail";
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
            @RequestParam("leftImage") MultipartFile leftImage,
            @RequestParam("rightImage") MultipartFile rightImage,
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
                urlSlug,
                logoImage,
                mainImage,
                leftImage,
                rightImage,
                homepageUrl,
                snsUrl,
                mapUrl,
                slideImages
        );
        return "Company successfully registered.";
    }
	
	@GetMapping("/checkUrl")
	public ResponseEntity<Map<String, Boolean>> checkUrlSlug(@RequestParam String urlSlug) {
        boolean exists = companyRepository.findByUrlSlug(urlSlug).isPresent();
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
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

