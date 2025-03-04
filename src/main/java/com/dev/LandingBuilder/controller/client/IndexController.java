package com.dev.LandingBuilder.controller.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dev.LandingBuilder.model.client.Client;
import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.service.SMSService;
import com.dev.LandingBuilder.service.client.ClientService;
import com.dev.LandingBuilder.service.client.CompanyService;
import com.dev.LandingBuilder.service.client.FormFieldService;
import com.dev.LandingBuilder.service.client.InquiryService;
import com.dev.LandingBuilder.service.client.SelectOptionService;

@Controller
public class IndexController {

	@Autowired
    private CompanyService companyService;
	
	@Autowired
	private CompanyRepository companyRepository;

    @Autowired
    private FormFieldService formFieldService;

    @Autowired
    private SelectOptionService selectOptionService;
	
    @Autowired
    private InquiryService inquiryService;
	
    @Autowired
    private SMSService smsService;
    
    @Autowired
    private ClientService clientService;
    
	@GetMapping({"/", "", "/index"})
	public String index(
			Model model
			) {
		
		model.addAttribute("company", companyRepository.findAll());
		return "front/index";
	}
	
	@GetMapping("/{slugUrl}")
	public String slugUrl(
			@PathVariable("slugUrl") String slugUrl,
			Model model) {

		Optional<Company> optionalCompany = companyService.getCompanyByUrlSlug(slugUrl);
	
		List<String> reservedUrls = List.of("admin", "manager", "dashboard");
		if (reservedUrls.contains(slugUrl.toLowerCase())) {
	        return "redirect:/" + slugUrl;
	    }
		
	    if (optionalCompany.isEmpty()) {
	        // 회사 정보가 없으면 "front/index" 반환
	        return "redirect:/";
	    }
	    // 2. 폼 필드 조회
	    var formFields = formFieldService.getFormFieldsByCompanyId(optionalCompany.get().getId());

	    // 3. 각 폼 필드의 SelectOption 조회
	    var formFieldsWithOptions = formFields.stream().map(formField -> {
	        var options = selectOptionService.getSelectOptionsByFormFieldId(formField.getId());
	        formField.setSelectOptions(options);
	        return formField;
	    }).toList();

	    // 4. 모델에 데이터 추가
	    model.addAttribute("company", optionalCompany.get());
	    model.addAttribute("formFields", formFieldsWithOptions);

	    // 5. "front/conference"로 반환
	    return "front/conference";
	}
	
	@PostMapping("/inquiryInsert")
	@ResponseBody
	public String insertInquiry(@RequestParam Map<String, String> formData) throws EncoderException {
	    
	    String url = companyRepository.findById(Long.parseLong(formData.get("companyId")))
	                                  .map(Company::getUrlSlug)
	                                  .orElse("/");
	    
	    smsService.sendMessage("010-7508-3197", companyRepository.findById(Long.parseLong(formData.get("companyId"))).get().getName()
	    		+ " 업체의 KBT HUB 디비 들어왔습니다.");

	    String msg = "申し込み頂きありがとうこざいます。担当のものが確認後、折り返しご連絡させて頂きます。";
	    inquiryService.saveInquiry(formData);
	    
	    // JavaScript에서 깨지지 않도록 안전하게 변환
	    StringBuilder sb = new StringBuilder();
	    sb.append("<script>");
	    sb.append("alert('" + msg + "');");
	    sb.append("location.href='/" + url + "';");
	    sb.append("</script>");

	    return sb.toString();
	}
	
	@PostMapping("/insert")
	@ResponseBody
	public String clientInsert(
			@RequestBody Client client,
			Model model
			) throws EncoderException {
		
		smsService.sendMessage("010-7508-3197", "지우의원 어필리에이트 KBT HUB 디비 들어왔습니다.");
		
		if(clientService.clientInsert(client)) {
			return "success";
		}else {
			return "fail";

		}
	}

}
