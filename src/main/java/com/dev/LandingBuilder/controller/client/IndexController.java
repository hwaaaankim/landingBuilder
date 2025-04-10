package com.dev.LandingBuilder.controller.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.dev.LandingBuilder.model.client.Client;
import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.service.SMSService;
import com.dev.LandingBuilder.service.client.ClientService;
import com.dev.LandingBuilder.service.client.CompanyService;
import com.dev.LandingBuilder.service.client.FormFieldService;
import com.dev.LandingBuilder.service.client.InquiryService;
import com.dev.LandingBuilder.service.client.SelectOptionService;

import jakarta.servlet.http.HttpServletRequest;

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
	public String slugUrl(@PathVariable("slugUrl") String slugUrl,
	                      Model model,
	                      HttpServletRequest request) {
	    if (List.of("admin", "manager", "dashboard").contains(slugUrl.toLowerCase())) {
	        return "redirect:/" + slugUrl;
	    }

	    Optional<Company> optionalCompany = companyService.getCompanyByUrlSlug(slugUrl);
	    if (optionalCompany.isEmpty()) return "redirect:/";
	    Company company = optionalCompany.get();

	    var formFields = formFieldService.getFormFieldsByCompanyId(company.getId());
	    var formFieldsWithOptions = formFields.stream().map(field -> {
	        var options = selectOptionService.getSelectOptionsByFormFieldId(field.getId());
	        field.setSelectOptions(options);
	        return field;
	    }).toList();

	    // ✅ FlashAttribute 꺼내기
	    Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

	    String scriptVersion = "1"; // 기본값
	    String alertMessage = null;
	    String userId = null;

	    if (flashMap != null) {
	        Object sv = flashMap.get("scriptVersion");
	        if (sv instanceof String && !((String) sv).isBlank()) {
	            scriptVersion = (String) sv;
	        }

	        Object msg = flashMap.get("alertMessage");
	        if (msg instanceof String) {
	            alertMessage = (String) msg;
	        }

	        Object uid = flashMap.get("userId");
	        if (uid instanceof String) {
	            userId = (String) uid;
	        }
	    }
	    System.out.println(scriptVersion);
	    // ✅ 스크립트 치환 처리
	    String scriptRaw = "1".equals(scriptVersion)
	        ? company.getScriptOne()
	        : company.getScriptTwo();

	    String finalScript = (userId != null && scriptRaw != null)
	        ? scriptRaw.replace("__USER_ID__", userId)
	        : scriptRaw;

	    model.addAttribute("company", company);
	    model.addAttribute("formFields", formFieldsWithOptions);
	    model.addAttribute("scriptContent", finalScript);

	    if (alertMessage != null && !alertMessage.isBlank()) {
	        model.addAttribute("alertMessage", alertMessage);
	    }

	    return "front/conference";
	}

	
	@PostMapping("/inquiryInsert")
	public String insertInquiry(@RequestParam Map<String, String> formData,
	                            RedirectAttributes redirectAttributes) throws EncoderException {

	    Long companyId = Long.parseLong(formData.get("companyId"));
	    Company company = companyRepository.findById(companyId).orElse(null);
	    if (company == null) return "redirect:/";

	    String slugUrl = company.getUrlSlug();

	    // ✅ 유저 ID 생성
	    String userId = generateUserId(slugUrl);

	    // ✅ 서비스 호출 (userId 포함)
	    inquiryService.saveInquiry(formData, userId);

	    smsService.sendMessage("010-7508-3197",
	        company.getName() + " 업체의 KBT HUB 디비 들어왔습니다.");

	    // ✅ 리다이렉트에 전달할 flash 값 설정
	    redirectAttributes.addFlashAttribute("scriptVersion", "2");
	    redirectAttributes.addFlashAttribute("alertMessage",
	        "申し込み頂きありがとうこざいます。担当のものが確認後、折り返しご連絡させて頂きます。");
	    redirectAttributes.addFlashAttribute("userId", userId);

	    return "redirect:/" + slugUrl;
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
	public String generateUserId(String slugUrl) {
	    String timestamp = LocalDateTime.now()
	        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

	    String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	    String raw = timestamp + random;

	    // SHA-256 해시 생성
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
	        String base64 = Base64.getEncoder().encodeToString(hash)
	                              .replaceAll("[^a-zA-Z0-9]", "")
	                              .substring(0, 12);
	        return slugUrl + "_" + base64;
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("SHA-256 algorithm not found", e);
	    }
	}

}
