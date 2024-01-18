
package com.cooperative.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.cooperative.entity.Cooperative;
import com.cooperative.entity.Role;
import com.cooperative.entity.User;
import com.cooperative.model.CooperativeModel;
import com.cooperative.model.UserModel;
import com.cooperative.service.CommonService;
import com.cooperative.utility.StaticData;

@RestController
@Transactional
public class UserController {
	@Autowired
	private CommonService commonService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loadLoginPage(Model model,HttpSession session,Locale locale) {
		ModelAndView modelAndView = new ModelAndView("Login");
		modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
		if (session.getAttribute("usmId")!= null){
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logoutApp(HttpSession session,Locale locale) {
		session.invalidate();
		ModelAndView modelAndView = new ModelAndView("Login");
		return modelAndView;
	}
	

	// Check Login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView checkLogin(UserModel userModel, Model model,HttpSession session,Locale locale) {
		ModelAndView modelAndView = new ModelAndView("index");// from tiles

		User user = (User) commonService.fetchObjectByNamedQuery("checkUser", "userName="+userModel.getUserName()+"=string&"
				+ "password="+userModel.getPassword()+"=string");
		if(user!=null){
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("userName", user.getUserName());
			session.setAttribute("firstName", user.getFirstName());
			session.setAttribute("lastName", user.getLastName());
			session.setAttribute("roleId", user.getRoleId().getRoleId());
			if (user.getCopId()!= null)
				session.setAttribute("copId", user.getCopId().getCopId());

			modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
				
			List<Object[]> lstAllDocuments = commonService.fetchByNativeQuery("select count(doc_id) as total,"
					+ " sum(case when api_status = '200' then 1 else 0 end) as passed,"
					+ " sum(case when api_status <> '200' then 1 else 0 end) as failed from tbl_document");
			
			model.addAttribute("total", lstAllDocuments.get(0)[0]);
			model.addAttribute("total_success", lstAllDocuments.get(0)[1]);
			model.addAttribute("total_failed", lstAllDocuments.get(0)[2]);
		}else{
			modelAndView = new ModelAndView("Login");
		}
		
		return modelAndView;
	}

		@RequestMapping(value = "/cooperative", method = RequestMethod.GET)
		public ModelAndView loadCooperative(Model model,HttpSession session,Locale locale) {
			ModelAndView modelAndView = new ModelAndView("cooperative");// from tiles
			modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
			if (session.getAttribute("userId")!= null){
				List<Cooperative> lstCooperative = commonService.fetchByNamedQuery("fetchCooprativeList","");
				model.addAttribute("lstCooperative", lstCooperative);
			}
			return modelAndView;
		}


		@RequestMapping(value = "/cooperative", method = RequestMethod.POST)
		public ModelAndView saveCooperative(@ModelAttribute  CooperativeModel copModel, Model model, HttpSession session, Locale locale) {
			ModelAndView modelAndView = new ModelAndView("cooperative");
			modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
			String str = null;
			if (session.getAttribute("userId")!= null){
				try {
					Cooperative cooperative = null;
					if (copModel.getCopId() != null) {
						cooperative = (Cooperative) commonService.findById(Cooperative.class,copModel.getCopId());
						cooperative.setUpdatedBy((Long)session.getAttribute("userId"));
						cooperative.setUpdatedDate(new Date());
						str = "Record updated successfully";
					} else {
						cooperative = new Cooperative();
						cooperative.setCreatedBy((Long)session.getAttribute("userId"));
						cooperative.setCreatedDate(new Date());
						str = "New record added successfully";
					}
					cooperative.setCopAddress(copModel.getCopAddress());
					cooperative.setCopName(copModel.getCopName());
					cooperative.setCopRegNo(copModel.getCopRegNo());
					cooperative.setIsActive('Y');
					commonService.saveOrUpdate(cooperative);
				} catch (Exception e) {
					e.printStackTrace();
					str = "Transaction Failed";
				}
			}
			List<Cooperative> lstCooperative = commonService.fetchByNamedQuery("fetchCooprativeList","");
			model.addAttribute("lstCooperative", lstCooperative);
			model.addAttribute("msg", str);
			return modelAndView;
		}

		@RequestMapping(value = "/loadCooperativeDetails", method = RequestMethod.GET)
		public @ResponseBody Map<String, Object> loadCooperativeDetails(Long copId) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				if (copId != null) {
					Cooperative cooperative = (Cooperative) commonService.findById(Cooperative.class,copId);
					if(cooperative!=null){
						map.put("copModel", cooperative);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
		
		@RequestMapping(value = "/activateDeactivateCoperative", method = RequestMethod.POST)
		public @ResponseBody Map<String, Object> activateDeactivateCoperative(Long copId) {
			Map<String, Object> map = new HashMap<String, Object>();
			String str = null;
			try {
				if (copId != null) {
					Cooperative cooperative = (Cooperative) commonService.findById(Cooperative.class,copId);
					
					if (cooperative.getIsActive() != null && cooperative.getIsActive() == 'Y')
						cooperative.setIsActive('N');
					else if (cooperative.getIsActive() == null || cooperative.getIsActive() == 'N')
						cooperative.setIsActive('Y');
					
					commonService.saveOrUpdate(cooperative);
					str = "Record updated successfully";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				str = "Transaction Failed";
			}
			map.put("message", str);
			return map;
		}
			
			@RequestMapping(value = "/userRegistration", method = RequestMethod.GET)
			public ModelAndView userList(Model model,HttpSession session,Locale locale) {
				ModelAndView modelAndView = new ModelAndView("UserRegistration");// from tiles
				modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
				if (session.getAttribute("userId")!= null){
					List<User> lst = commonService.fetchByNamedQuery("fetchUserList","");
					model.addAttribute("lstUser", lst);
					List<Role> lst1 =commonService.fetchByNamedQuery("fetchActiveRoleList","");
					model.addAttribute("lstRole", lst1);
					List<Cooperative> lstCooperative =commonService.fetchByNamedQuery("fetchActiveCooperativeList","");
					model.addAttribute("lstCooperative", lstCooperative);
				}
				return modelAndView;
			}


			@RequestMapping(value = "/userRegistration", method = RequestMethod.POST)
			public ModelAndView addEditUser(@ModelAttribute  UserModel userModel, Model model,HttpSession session,Locale locale) {
				ModelAndView modelAndView = new ModelAndView("UserRegistration");// from tiles
				modelAndView = StaticData.loadDataForHomepage(session, model, locale, modelAndView);
				String str = null;
				if (session.getAttribute("userId")!= null){
					try {
						User user = null;
						if (userModel.getUserId() != null) {
							user = (User) commonService.findById(User.class,userModel.getUserId());
							user.setUpdatedBy((Long)session.getAttribute("userId"));
							user.setUpdatedDate(new Date());
							str = "Record updated successfully";
						} else {
							user = new User();
							user.setCreatedBy((Long)session.getAttribute("userId"));
							user.setCreatedDate(new Date());
							str = "New record added successfully";
						}
						user.setUserName(userModel.getUserName());
						user.setPassword(userModel.getPassword());
						user.setFirstName(userModel.getFirstName());
						user.setMiddleName(userModel.getMiddleName());
						user.setLastName(userModel.getLastName());
						user.setEmail(userModel.getEmail());
						user.setMobileNo(userModel.getMobileNo());
						if(userModel.getRoleId()!=null){
							user.setRoleId((Role)  commonService.findById(Role.class,userModel.getRoleId()));
						}
						if(userModel.getCopId()!=null){
							user.setCopId((Cooperative)  commonService.findById(Cooperative.class,userModel.getCopId()));
						}
						user.setIsActive('Y');
						commonService.saveOrUpdate(user);
					} catch (Exception e) {
						e.printStackTrace();
						str = "Transaction Failed";
					}
				}
				List<User> lst = commonService.fetchByNamedQuery("fetchUserList","");
				model.addAttribute("lstUser", lst);
				List<Role> lst1 =commonService.fetchByNamedQuery("fetchActiveRoleList","");
				model.addAttribute("lstRole", lst1);
				List<Cooperative> lstCooperative =commonService.fetchByNamedQuery("fetchActiveCooperativeList","");
				model.addAttribute("lstCooperative", lstCooperative);
				
				model.addAttribute("msg", str);
				return modelAndView;
			}

			// Get User Details by ID
			@RequestMapping(value = "/loadUserDetails", method = RequestMethod.GET)
			public @ResponseBody Map<String, Object> loadItemDetails(Long userId) {
				Map<String, Object> map = new HashMap<String, Object>();
				try {
					if (userId != null) {
						User user= (User) commonService.findById(User.class, userId);
						if(user!=null){
							map.put("userModel", user);
						}
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return map;
			}
			
			// Activate / Deactive User
			@RequestMapping(value = "/activateDeactivateUser", method = RequestMethod.POST)
			public @ResponseBody Map<String, Object> activateDeactivateUser(Long userId) {
				Map<String, Object> map = new HashMap<String, Object>();
				String str = null;
				try {
					User user = null;
					if (userId != null) {
						user = (User) commonService.findById(User.class,userId);
						user.setUpdatedDate(new Date());
						if (user.getIsActive() != null && user.getIsActive() == 'Y')
							user.setIsActive('N');
						else if (user.getIsActive() == null || user.getIsActive() == 'N')
							user.setIsActive('Y');
						commonService.saveOrUpdate(user);
						str = "Record updated successfully";
					}
				} catch (Exception e) {
					e.printStackTrace();
					str = "Transaction Failed";
				}
				map.put("message", str);
				return map;
			}

}
