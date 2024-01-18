package com.cooperative.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.cooperative.entity.Cooperative;
import com.cooperative.entity.Document;
import com.cooperative.entity.SystemConfiguration;
import com.cooperative.entity.Wallet;
import com.cooperative.model.AadharData;
import com.cooperative.model.AadharResponse;
import com.cooperative.model.UserModel;
import com.cooperative.service.CommonService;
import com.cooperative.utility.PropertyFetcher;
import com.cooperative.utility.StaticData;
import com.cooperative.utility.WebConstants.FileName;

@RestController
@Transactional
public class DashboardController {
	
	@Autowired
	private CommonService commonService;
	
	private static String API_URL = PropertyFetcher.getPropertyValue(FileName.APPLICATION_CONFIG, "AADHAR_API_URL");
	private static String TOKEN = PropertyFetcher.getPropertyValue(FileName.APPLICATION_CONFIG, "TOKEN");
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView loadIndexPage(Model model,HttpSession session,Locale locale) {
		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
		if (session.getAttribute("userId")!= null){
			
			List<Object[]> lstAllDocuments = commonService.fetchByNativeQuery("select count(doc_id) as total,"
					+ " sum(case when api_status = '200' then 1 else 0 end) as passed,"
					+ " sum(case when api_status <> '200' then 1 else 0 end) as failed from tbl_document");
			
			model.addAttribute("total", lstAllDocuments.get(0)[0]);
			model.addAttribute("total_success", lstAllDocuments.get(0)[1]);
			model.addAttribute("total_failed", lstAllDocuments.get(0)[2]);
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/document", method = RequestMethod.GET)
	public ModelAndView loadTrackDocument(Model model,HttpSession session,Locale locale) {
		ModelAndView modelAndView = new ModelAndView("document");
		modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
		
		if (session.getAttribute("userId")!= null){
			List<SystemConfiguration> lstDocumentType = commonService.findByConfigType("Document_Type");
			model.addAttribute("lstDocumentType", lstDocumentType);
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/loadDocumentDetails", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> loadDocumentDetails(String docNumber,HttpSession session, Long docTypeId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Long copId  = (Long) session.getAttribute("copId");
		ResponseEntity<AadharResponse> apiResponse =null;
		try {
			 
			if (docTypeId != null && docTypeId.equals(1l)) {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.APPLICATION_JSON);
			    headers.set("Authorization", TOKEN);
			    JSONObject doc = new JSONObject();
			    doc.put("id_number", docNumber);
			    HttpEntity<String> request = new HttpEntity<String>(doc.toString(), headers);
			    
			    apiResponse = restTemplate.postForEntity(API_URL, request, AadharResponse.class);
			}
		} catch (Exception e) {
			if(e.getMessage().contains("422 UNPROCESSABLE ENTITY")){
				AadharResponse response = new AadharResponse();
				response.setStatus_code("422");
				response.setMessage("Verification Failed.");
				response.setData(new AadharData(docNumber,"false","invalid_aadhaar_format"));
				apiResponse = new ResponseEntity<AadharResponse>(response, HttpStatus.UNPROCESSABLE_ENTITY);
			}else 
				e.printStackTrace();	
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			String response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiResponse.getBody());
			
			Document document = new Document();
			document.setCopId((Cooperative) commonService.findById(Cooperative.class, copId));
			document.setCreatedDate(new Date());
			document.setCreatedBy((Long) session.getAttribute("userId"));
			document.setDocTypeId((SystemConfiguration) commonService.findById(SystemConfiguration.class, docTypeId));
			document.setApiResponse(response);
			document.setDocNumber(docNumber);
			document.setApiStatus(apiResponse.getStatusCode().toString());
			commonService.saveOrUpdate(document);
			
			Wallet wallet = (Wallet) commonService.fetchObjectByNamedQuery("fetchWalletByCooprative", "copId="+copId+"=long");
			if(wallet != null){
				wallet.setBalancedAmount(wallet.getBalancedAmount()-10d);
				commonService.saveOrUpdate(wallet);
			}
			map.put("response", response);
			
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@RequestMapping(value = "/wallet", method = RequestMethod.GET)
	public ModelAndView loadWallet(Model model,HttpSession session,Locale locale) {
		ModelAndView modelAndView = new ModelAndView("wallet");
		modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
		Long copId  = (Long) session.getAttribute("copId");
		Wallet wallet = (Wallet) commonService.fetchObjectByNamedQuery("fetchWalletByCooprative", "copId="+copId+"=long");
		if(wallet != null){
			model.addAttribute("walletAmount", wallet.getBalancedAmount());
		}else {
			model.addAttribute("walletAmount", 0l);
		}
		List<Cooperative> lstCooperative =commonService.fetchByNamedQuery("fetchActiveCooperativeList","");
		model.addAttribute("lstCooperative", lstCooperative);
		
		List<Wallet> lstWallet =commonService.fetchByNamedQuery("fetchAllWalletByCooprative","");
		model.addAttribute("lstWallet", lstWallet);
		return modelAndView;
	}
	
	@RequestMapping(value = "/wallet", method = RequestMethod.POST)
	public ModelAndView saveWallet(@ModelAttribute UserModel userModel, Model model,HttpSession session,Locale locale) {
		ModelAndView modelAndView = new ModelAndView("wallet");
		modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
		
		try {
			if (session.getAttribute("userId")!= null){
				Wallet wallet = (Wallet) commonService.fetchObjectByNamedQuery("fetchWalletByCooprative", "copId="+userModel.getCopId()+"=long");
				if(wallet == null){
					wallet = new Wallet(); 
					wallet.setAmount(userModel.getAmount());
					wallet.setBalancedAmount(userModel.getAmount());
					wallet.setCopId((Cooperative)  commonService.findById(Cooperative.class,userModel.getCopId()));
					wallet.setIsActive('Y');
				} else {
					wallet.setAmount(wallet.getAmount() + userModel.getAmount());
					wallet.setBalancedAmount(wallet.getBalancedAmount() + userModel.getAmount());
				}
				commonService.saveOrUpdate(wallet);
			}
			List<Cooperative> lstCooperative =commonService.fetchByNamedQuery("fetchActiveCooperativeList","");
			model.addAttribute("lstCooperative", lstCooperative);
			
			List<Wallet> lstWallet =commonService.fetchByNamedQuery("fetchAllWalletByCooprative","");
			model.addAttribute("lstWallet", lstWallet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
		
}
