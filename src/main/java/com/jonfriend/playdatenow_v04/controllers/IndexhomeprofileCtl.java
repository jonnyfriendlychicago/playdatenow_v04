package com.jonfriend.playdatenow_v04.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

//import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.jonfriend.playdatenow_v04.dataTransferObjects.UserUpdateDto;
import com.jonfriend.playdatenow_v04.models.LoginUserMdl;
import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
//import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
//import com.jonfriend.playdatenow_v04.models.StateterritoryMdl;
import com.jonfriend.playdatenow_v04.models.UserMdl;
//import com.jonfriend.playdatenow_v04.models.UserUpdateMdl;
//import com.jonfriend.playdatenow_v04.models.UserupdateMdl;
import com.jonfriend.playdatenow_v04.services.PlaydateSrv;
//import com.jonfriend.playdatenow_v04.services.StateterritorySrv;
import com.jonfriend.playdatenow_v04.services.UserSrv;

@Controller
public class IndexhomeprofileCtl {
	
	@Autowired
	private UserSrv userSrv;
	
	@Autowired
	private PlaydateSrv playdateSrv;
	
//	@Autowired
//	private StateterritorySrv stateterritorySrv;
	
	 
// ********************************************************************
// AUTHENTICATION METHODS
// ********************************************************************
	
	@GetMapping("/")
	public String index(
			Model model
			, HttpSession session) {
		
		if(session.getAttribute("userId") != null) {return "redirect:/home";} // *** Redirect authorized users to the /home METHOD -- DON'T EXPOSE REG/LOGIN index page TO ALREADY AUTH'ED USERS ***
		model.addAttribute("newLogin", new LoginUserMdl()); // putting a new empty LoginUserMdl obj on the index page,
		return "index.jsp"; 
	}
 
    @PostMapping("/login")
    public String login(
    		@Valid @ModelAttribute("newLogin") LoginUserMdl newLogin
    		, BindingResult result
    		, Model model
    		, HttpSession session
    		) {
    	
    	UserMdl user = userSrv.login(newLogin, result);
    	
        if(result.hasErrors() || user==null ) // user==null is the equiv of "user name not found!"
        {
        	model.addAttribute("newUser", new UserMdl()); //deliver the empty UserMdl object before re-rendering the reg/login page; the LoginUserMdl obj will maintain the incoming values to this method
        	model.addAttribute("validationErrorMsg", "Login errors.  See details in form below and try again.");
            return "index.jsp";
        }
    
        session.setAttribute("userId", user.getId()); // No errors?  Store the ID from the DB in session.
	    return "redirect:/playdate"; // redirecting here to playdate for now, b/c insuff time to build out dashboard/home-style page
    }
     
	@GetMapping("/register")
	public String register(
			Model model
			, HttpSession session) {
		
		if(session.getAttribute("userId") != null) {return "redirect:/home";} // redirect authorized users to the /home METHOD; don't expose the index page to already-authenticated users
        model.addAttribute("newUser", new UserMdl()); // login/reg form items: putting a new empty UserMdl obj for on the index page, so user can shove data into it using the form.
		return "register.jsp"; 
	}
	
    @PostMapping("/register")
    public String register(
    		@Valid @ModelAttribute("newUser") UserMdl newUser
    		, BindingResult result
    		, Model model
    		, HttpSession session
    		, RedirectAttributes redirectAttributes
    		) {
        
    	UserMdl user = userSrv.register(newUser, result);
        
        if(result.hasErrors()) {
        	model.addAttribute("validationErrorMsg", "Registration errors.  See details in form below and try again.");
            return "register.jsp";
        }
        
        session.setAttribute("userId", user.getId());  // this is a repeat of the last line of the login method
        redirectAttributes.addFlashAttribute("successMsg", "Welcome to PlayDateNow.  Take a moment to complete your profile: click on your name on the top right >> then click Profile.  Below: browse playdates and create your own.");
	    return "redirect:/playdate"; // redirecting here to playdate for now, b/c insuff time to build out dashboard/home-style page
    }
     
    @GetMapping("/logout")
	public String logout(
			HttpSession session
			) {
    	session.setAttribute("userId", null); // nulls the session.userId value, which prevents access to any/all page(s) other than index, thus redirect to index.
    	System.out.println("User logged out."); 
	    return "redirect:/";
	}

//********************************************************************
// HOME/PROFILE/ETC METHODS
//********************************************************************
		
	    @GetMapping("/home")
		public String home(
				Model model
				, HttpSession session
				) {
		 
	    	// log out the unauth vs. deliver the auth user data
			if(session.getAttribute("userId") == null) {return "redirect:/logout";}
			Long userId = (Long) session.getAttribute("userId");
			model.addAttribute("authUser", userSrv.findById(userId));
			
			System.out.println("Page Display: Home"); 
		    return "redirect:/playdate"; // redirecting here to playdate for now, b/c insuff time to build out dashboard/home-style page
		}

		// view all profile
		@GetMapping("/profile")
		public String showAllprofile(
				Model model
				, HttpSession session
				) {
			
			// log out the unauth / deliver the auth use data
			if(session.getAttribute("userId") == null) {return "redirect:/logout";}
			Long authenticatedUserId = (Long) session.getAttribute("userId");
			model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
			
			List<UserMdl> profileList = userSrv.returnAll();
			model.addAttribute("profileList", profileList);
			return "profile/list.jsp";
		}
		
		// display profile page
		@GetMapping("/profile/{id}")
		public String showProfile(
				@PathVariable("id") Long userProfileId
				, Model model
				, HttpSession session
				) {
			
	    	// log out the unauth vs. deliver the auth user data
			if(session.getAttribute("userId") == null) {return "redirect:/logout";}
			Long userId = (Long) session.getAttribute("userId");
			model.addAttribute("authUser", userSrv.findById(userId));
			
			// grab the entire user object using the url parameter, then deliver to page
			UserMdl userObj = userSrv.findById(userProfileId);
			model.addAttribute("userProfile", userObj); 
			
			// list of playdates where createdBy_id = userProfileId -- CURRENT AND FUTURE
			List<PlaydateMdl> userHostedPlaydateListCurrentPlus = playdateSrv.userHostedPlaydateListCurrentPlus(userProfileId);
			model.addAttribute("userHostedPlaydateListCurrentPlus", userHostedPlaydateListCurrentPlus);
			
			// list of playdates where createdBy_id = userProfileId -- PAST
			List<PlaydateMdl> userHostedPlaydateListPast = playdateSrv.userHostedPlaydateListPast(userProfileId);
			model.addAttribute("userHostedPlaydateListPast", userHostedPlaydateListPast);
			
//			Date todayDate = new Date(); // not sure why this here, I think it was for testing; 9/12 
			return "profile/record.jsp";
		}
		
		// display edit page
		@GetMapping("/profile/{id}/edit")
		public String editProfile(
//				@ModelAttribute("userProfileTobe") UserupdateMdl userupdateObj
				@ModelAttribute("userProfileTobe") UserUpdateDto userupdateObj
				, @PathVariable("id") Long userProfileId
				, Model model
				, HttpSession session
				) {
			
			// log out the unauth / deliver the auth use data
			if(session.getAttribute("userId") == null) {return "redirect:/logout";}
			Long userId = (Long) session.getAttribute("userId");
			model.addAttribute("authUser", userSrv.findById(userId)); 

			// (1) acquire as-is object/values from db
			UserMdl userObj = userSrv.findById(userProfileId); 
			
			// (2) Populate the initially-empty userupdateObj (being sent to the form) with the values from the existing record
			userupdateObj.setAboutMe(userObj.getAboutMe()); 
			userupdateObj.setLastName(userObj.getLastName()); 
			userupdateObj.setUserName(userObj.getUserName()); 
			userupdateObj.setEmail(userObj.getEmail()); 
			userupdateObj.setFirstName(userObj.getFirstName()); 
			userupdateObj.setCity(userObj.getCity()); 
			userupdateObj.setZipCode(userObj.getZipCode()); 
			
			// (3) not related to above, but easily confused: 
			
			model.addAttribute("userProfile", userObj); // send the as-is object to the page, so static values can be used (createdAt, Id, etc.)

			return "profile/edit.jsp";
		}
		
		// process the edit
		@PostMapping("/profile/edit")
		public String PostTheEditProfile(
				@Valid 
//				@ModelAttribute("userProfileTobe") UserupdateMdl userupdateObj
				@ModelAttribute("userProfileTobe") UserUpdateDto userupdateObj
				, BindingResult result
				, Model model
				, HttpSession session
				, RedirectAttributes redirectAttributes
				) {
			
			// log out the unauth / deliver the auth use data
			if(session.getAttribute("userId") == null) {return "redirect:/logout";}
			Long authenticatedUserId = (Long) session.getAttribute("userId");
			model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
			
			// (1) acquire as-is object/values from db (as you did in the display mthd)
			UserMdl userObj = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
			
			// (2) overwrite the targeted fields in the userObj with values from the userupdateObj
			userObj.setEmail(userupdateObj.getEmail()); 
			userObj.setUserName(userupdateObj.getUserName()); 
			userObj.setFirstName(userupdateObj.getFirstName() ); 
			userObj.setLastName(userupdateObj.getLastName() ); 
			
			userObj.setAboutMe(userupdateObj.getAboutMe() ); 
			userObj.setCity(userupdateObj.getCity() );  
			userObj.setZipCode(userupdateObj.getZipCode() ); 
			
			// (3) run the service to save the updated object
			userSrv.updateUserProfile(userObj, result);
			
			if (result.hasErrors() ) { 
				model.addAttribute("userProfile", userObj); // send the as-is object to the page, so static values can be used (createdAt, Id, etc.)
				return "profile/edit.jsp";
			} else {
				return "redirect:/profile/" + userObj.getId(); 
			}
		}
// end of methods
}
