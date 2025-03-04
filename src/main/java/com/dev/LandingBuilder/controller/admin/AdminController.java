package com.dev.LandingBuilder.controller.admin;

import java.io.IOException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dev.LandingBuilder.dto.FormFieldDTO;
import com.dev.LandingBuilder.dto.InquiryRequestDTO;
import com.dev.LandingBuilder.model.client.Client;
import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.model.client.FieldType;
import com.dev.LandingBuilder.model.client.Inquiry;
import com.dev.LandingBuilder.model.client.InquiryData;
import com.dev.LandingBuilder.repository.ClientRepository;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.repository.InquiryRepository;
import com.dev.LandingBuilder.repository.SelectOptionRepository;
import com.dev.LandingBuilder.service.client.ClientService;
import com.dev.LandingBuilder.service.client.CompanyService;
import com.dev.LandingBuilder.service.client.FormFieldService;
import com.dev.LandingBuilder.service.client.InquiryService;

import jakarta.servlet.http.HttpServletRequest;

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
    
    @Autowired
	ClientRepository clientRepository;
	
	@Autowired
	ClientService clientService;

	@Autowired
	SelectOptionRepository selectOptionRepository;
	
	@GetMapping({"/insertClientForm", "", "/"})
	public String insertClientForm() {
		
		return "administration/insertClientForm";
	}
	
	@GetMapping("/clientManager")
	public String clientManager(
	        Model model,
	        @PageableDefault(size = 15) Pageable pageable
	) {
	    // Inquiry 목록을 최신순으로 조회
	    Page<Inquiry> inquiries = inquiryRepository.findAllByOrderByIdDesc(pageable);

	    // SELECT 타입의 필드 값을 optionValue로 변환
	    for (Inquiry inquiry : inquiries.getContent()) {
	        for (InquiryData data : inquiry.getInquiryData()) {
	            if (data.getFormField().getFieldType() == FieldType.SELECT) {
	                try {
	                    Long optionId = Long.parseLong(data.getFieldValue());
	                  
	                    
	                    selectOptionRepository.findById(optionId)
	                        .ifPresent(option -> data.setFieldValue(option.getOptionValue()));
	                    System.out.println(data.getFieldValue());
	                } catch (NumberFormatException e) {
	                    // 숫자로 변환할 수 없는 경우 로그 출력 (예외 방지)
	                    System.out.println("Invalid fieldValue for SelectOption: " + data.getFieldValue());
	                }
	            }
	        }
	    }

	    // 페이징 처리
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
		
		Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Inquiry ID: " + id));

	    // SELECT 타입의 필드 값을 optionValue로 변환
	    for (InquiryData data : inquiry.getInquiryData()) {
	        if (data.getFormField().getFieldType() == FieldType.SELECT) {
	            try {
	                Long optionId = Long.parseLong(data.getFieldValue()); // String → Long 변환
	                selectOptionRepository.findById(optionId)
	                        .ifPresent(option -> data.setFieldValue(option.getOptionValue())); // optionValue 설정
	            } catch (NumberFormatException e) {
	                System.out.println("Invalid fieldValue for SelectOption: " + data.getFieldValue());
	            }
	        }
	    }

	    model.addAttribute("inquiry", inquiry);
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

	    // Find the company by slugUrl
	    Company company = companyRepository.findByUrlSlug(urlSlug)
	            .orElseThrow(() -> new RuntimeException("Company not found for slugUrl: " + urlSlug));

	    // Save form fields
	    formFieldService.saveFormFields(company, formFields);

	    return ResponseEntity.ok("Form fields saved successfully!");
	}
	
	@RequestMapping(value = {"/clientList", ""}, method = {RequestMethod.POST, RequestMethod.GET})
	public String clientList(
			HttpServletRequest request, 
			Model model, 
			RedirectAttributes redirect,
			@PageableDefault(size = 10) Pageable pageable, 
			@RequestParam(required = false) String searchType,
			@RequestParam(required = false) String searchSubject, 
			@RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String startDate, 
			@RequestParam(required = false) String endDate
			) throws ParseException {
		Page<Client> clients = null;


        switch (searchType != null ? searchType : "none") {
            case "name":
                clients = (searchWord == null || searchWord.isEmpty()) ?
                        clientRepository.findByOrderByInquiryDateDesc(pageable) :
                        clientRepository.findByNameOrderByInquiryDateDesc(pageable, searchWord);
                break;
            case "phone":
                clients = (searchWord == null || searchWord.isEmpty()) ?
                        clientRepository.findByOrderByInquiryDateDesc(pageable) :
                        clientRepository.findByPhoneOrderByInquiryDateDesc(pageable, searchWord);
                break;
            case "email":
                clients = (searchWord == null || searchWord.isEmpty()) ?
                        clientRepository.findByOrderByInquiryDateDesc(pageable) :
                        clientRepository.findByEmailOrderByInquiryDateDesc(pageable, searchWord);
                break;
            case "subject":
                clients = clientRepository.findBySubjectOrderByInquiryDateDesc(pageable, searchSubject);
                break;
            case "period":
                clients = clientService.findByInquiryDate(pageable, startDate, endDate);
                break;
            default:
                clients = clientRepository.findByOrderByInquiryDateDesc(pageable);
        }

		int startPage = Math.max(1, clients.getPageable().getPageNumber() - 4);
		int endPage = Math.min(clients.getTotalPages(), clients.getPageable().getPageNumber() + 4);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("clients", clients);
		model.addAttribute("searchType", searchType);
		model.addAttribute("searchSubject", searchSubject);
		return "administration/client/clientList";
	}
	
	@GetMapping("/clientListDetail/{id}")
	public String clientDetail(
			@PathVariable Long id, 
			HttpServletRequest request, 
			RedirectAttributes redirect,
			Model model
			) {
		Client c = clientRepository.findById(id).get();
		clientService.changeSign(c);
		model.addAttribute("client", c);
		return "admin/client/clientManageF";
	}

	@PostMapping("/clientUpdate")
	@ResponseBody
	public String clientUpdate(
			Client client
			) throws ParseException {

		StringBuffer sb = new StringBuffer();
		String msg = "";

		try {
			clientService.clientUpdate(client);
			msg = "수정이 완료 되었습니다";
		} catch (Exception e) {
			msg = "에러가 발생하였습니다. 다시 시도 해 주세요";
		}

		sb.append("alert('" + msg + "');");
		sb.append("location.href='/admin/clientList'");
		sb.append("</script>");
		sb.insert(0, "<script>");

		return sb.toString();
	}

	@GetMapping("/clientDelete/{id}")
	@ResponseBody
	public String clientDelete(
			Client client, 
			HttpServletRequest request, 
			RedirectAttributes redirect
			)
			throws ParseException {

		StringBuffer sb = new StringBuffer();
		String msg = "";

		try {
			clientRepository.delete(client);
			msg = "삭제가 완료 되었습니다";
		} catch (Exception e) {
			msg = "에러가 발생하였습니다. 다시 시도 해 주세요";
		}

		sb.append("alert('" + msg + "');");
		sb.append("location.href='/admin/clientList'");
		sb.append("</script>");
		sb.insert(0, "<script>");

		return sb.toString();
	}
}

