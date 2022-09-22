package com.jonfriend.playdatenow_v04.controllers;

import com.jonfriend.playdatenow_v04.dataTransferObjects.UserUpdateDto;
//import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
import com.jonfriend.playdatenow_v04.models.UserMdl;
//import com.jonfriend.playdatenow_v04.services.PlaydateSrv;
import com.jonfriend.playdatenow_v04.services.UserSrv;
import com.jonfriend.playdatenow_v04.validator.UserValidator;
//import com.jonfriend.playdatenow_v04.services.StateterritorySrv;
//import com.jonfriend.playdatenow_v04.models.LoginUserMdl;
//import com.jonfriend.playdatenow_v04.models.UserUpdateMdl;
//import com.jonfriend.playdatenow_v04.models.UserupdateMdl;
//import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
//import com.jonfriend.playdatenow_v04.models.StateterritoryMdl;

import java.security.Principal;
//import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;



@Controller
public class IndexhomeprofileCtl {
	
	@Autowired
	private UserSrv userSrv;
	
//	@Autowired
//	private PlaydateSrv playdateSrv;
	
	// begin ssp
	@Autowired
	private UserValidator userValidator;
	
	private boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || AnonymousAuthenticationToken.class.
				isAssignableFrom(authentication.getClass())) {
			return false;
		}
		return authentication.isAuthenticated();
	}
	// end ssp
	 
// ********************************************************************
// AUTHENTICATION METHODS
// ********************************************************************
	
//	// begin: legacy login 
//	@GetMapping("/")
//	public String index(
//			Model model
//			, HttpSession session) {
//		
//		if(session.getAttribute("userId") != null) {return "redirect:/home";} // *** Redirect authorized users to the /home METHOD -- DON'T EXPOSE REG/LOGIN index page TO ALREADY AUTH'ED USERS ***
//		model.addAttribute("newLogin", new LoginUserMdl()); // putting a new empty LoginUserMdl obj on the index page,
//		return "index.jsp"; 
//	}
// 
//    @PostMapping("/login")
//    public String login(
//    		@Valid @ModelAttribute("newLogin") LoginUserMdl newLogin
//    		, BindingResult result
//    		, Model model
//    		, HttpSession session
//    		) {
//    	
//    	UserMdl user = userSrv.login(newLogin, result);
//    	
//        if(result.hasErrors() || user==null ) // user==null is the equiv of "user name not found!"
//        {
//        	model.addAttribute("newUser", new UserMdl()); //deliver the empty UserMdl object before re-rendering the reg/login page; the LoginUserMdl obj will maintain the incoming values to this method
//        	model.addAttribute("validationErrorMsg", "Login errors.  See details in form below and try again.");
//            return "index.jsp";
//        }
//    
//        session.setAttribute("userId", user.getId()); // No errors?  Store the ID from the DB in session.
//	    return "redirect:/playdate"; // redirecting here to playdate for now, b/c insuff time to build out dashboard/home-style page
//    }
//    // end: legacy login
	
  @GetMapping("/login")
  public String login(
  		@ModelAttribute("user") UserMdl userMdl,
  		// jrf: above from platform, but since we have sep log and user pages, I dont' think above is needed here, b/c it's only for register
  		@RequestParam(value="error", required=false) String error, 
  		@RequestParam(value="logout", required=false) String logout, 
  		Model model
  		) {

  	if (isAuthenticated()) {
          return "redirect:/";
      }
  	
  	if(error != null) {
          model.addAttribute("errorMessage", "Invalid Credentials, Please try again.");
      }
      if(logout != null) {
          model.addAttribute("logoutMessage", "Logout Successful.");
      }
      return "login.jsp";
  }
    
//    // begin: legacy register
//	@GetMapping("/register")
//	public String register(
//			Model model
//			, HttpSession session) {
//		
//		if(session.getAttribute("userId") != null) {return "redirect:/home";} // redirect authorized users to the /home METHOD; don't expose the index page to already-authenticated users
//        model.addAttribute("newUser", new UserMdl()); // login/reg form items: putting a new empty UserMdl obj for on the index page, so user can shove data into it using the form.
//		return "register.jsp"; 
//	}
//	
//    @PostMapping("/register")
//    public String register(
//    		@Valid @ModelAttribute("newUser") UserMdl newUser
//    		, BindingResult result
//    		, Model model
//    		, HttpSession session
//    		, RedirectAttributes redirectAttributes
//    		) {
//        
//    	UserMdl user = userSrv.register(newUser, result);
//        
//        if(result.hasErrors()) {
//        	model.addAttribute("validationErrorMsg", "Registration errors.  See details in form below and try again.");
//            return "register.jsp";
//        }
//        
//        session.setAttribute("userId", user.getId());  // this is a repeat of the last line of the login method
//        redirectAttributes.addFlashAttribute("successMsg", "Welcome to PlayDateNow.  Take a moment to complete your profile: click on your name on the top right >> then click Profile.  Below: browse playdates and create your own.");
//	    return "redirect:/playdate"; // redirecting here to playdate for now, b/c insuff time to build out dashboard/home-style page
//    }
//    // end legacy register
	
//	@RequestMapping("/register")
	@GetMapping("/register")
    public String registerForm(
    		@Valid @ModelAttribute("user") UserMdl userMdl
    		) {
        // new begin:
    	if (isAuthenticated()) {
            return "redirect:/";
        }
    	// new end
    	return "register.jsp";
    }
     
    @PostMapping("/register")
    public String registerPost(
    		@Valid @ModelAttribute("user") UserMdl userMdl
    		, BindingResult result
    		, Model model
    		, HttpSession session
    		, HttpServletRequest request
    		) {
        
        userValidator.validate(userMdl, result);
        
     	String password = userMdl.getPassword(); // Store the password before it is encrypted
        
        if (result.hasErrors()) {
//        	return "loginPage.jsp"; // this line from platform, this doesn't seem right??
        	return "register.jsp";
        }
        
        // will this be the first user record?  if so, Make it... SUPER ADMIN!
        if(userSrv.allUsers().size()==0) {
//        	userSrv.newUser(userMdl, "ROLE_SUPER_ADMIN");  // this line temporarily replaced with below line, so that all users are the same
        	userSrv.newUser(userMdl, "ROLE_USER");
		} else {
			userSrv.newUser(userMdl, "ROLE_USER");
		}
        
        // Log in new user with the password we stored before encrypting it.  NOTE: the method listed immed below is built right after this mthd concludes
     	authWithHttpServletRequest(request, userMdl.getEmail(), password);
     		return "redirect:/";
     	}
    
    // We call this new method below to automatically log in newly registered users
 	public void authWithHttpServletRequest(
 			HttpServletRequest request
 			, String email
 			, String password
 			) {
 	    try {
 	        request.login(email, password);
 	    } catch (ServletException e) {
 	    	System.out.println("Error while login: " + e);
 	    }
    
 	// end of method
 	}
 	
// 	// begin: legacy logout
//    @GetMapping("/logout")
//	public String logout(
//			HttpSession session
//			) {
//    	session.setAttribute("userId", null); // nulls the session.userId value, which prevents access to any/all page(s) other than index, thus redirect to index.
//    	System.out.println("User logged out."); 
//	    return "redirect:/";
//	}
// 	// end: legacy logout
    
 	// JRF: temporarily disabling below user upgrade program
// 	// user upgrade program
//    @RequestMapping("/admin/{id}")
//    // JRF no idea (again...) why above says request mapping instead of post... doesn't make any sense, not sure if/how will work
//    // update on above: I think it's b/c they are doing this as a link instead of a form; same approach on the delete thing 
// 	public String makeAdmin(
// 			@PathVariable("id") Long userId
// 			, Model model
// 			) {
// 		
// 		UserMdl userMdl = userSrv.findById(userId);
// 		userSrv.upgradeUser(userMdl);
// 		
// 		model.addAttribute("userList", userSrv.allUsers());
// 		 
// 		return "redirect:/admin";
// 	}

//********************************************************************
// HOME/PROFILE/ETC METHODS
//********************************************************************
		
    
//	// begin: legacy home mthd
// 	@GetMapping("/home")
//	public String home(
//			Model model
//			, HttpSession session
//			) {
//	 
//    	// log out the unauth vs. deliver the auth user data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long userId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(userId));
//		
//		System.out.println("Page Display: Home"); 
//	    return "redirect:/playdate"; // redirecting here to playdate for now, b/c insuff time to build out dashboard/home-style page
//	}
// 	// end: legacy home mthd

    @GetMapping(value = {"/", "/home"})
    public String home(
    		Principal principal
    		, Model model
    		) {    	
         
        String email = principal.getName();
		UserMdl userMdl = userSrv.findByEmail(email);
		model.addAttribute("authUser", userMdl);
		
		
		// JRF temporarily removing below: updating last login and substituting admin.jsp for home is not desired
//		if(userMdl!=null) {
//			userMdl.setLastLogin(new Date());
//			userSrv.updateUser(userMdl);
//			// If the user is an ADMIN or SUPER_ADMIN they will be redirected to the admin page
//			if(userMdl.getRoleMdl().get(0).getName().contains("ROLE_SUPER_ADMIN") || userMdl.getRoleMdl().get(0).getName().contains("ROLE_ADMIN")) {
//				model.addAttribute("currentUser", userSrv.findByEmail(email));
//				model.addAttribute("userList", userSrv.allUsers());
//				return "admin.jsp";
//			}
//			// All other users are redirected to the home page
//		}
		
        return "home.jsp";
    }
    
    // JRF temporarily disabling this whole admin program
//  @RequestMapping("/admin")
//  public String adminDisplayPage(
//  		Principal principal
//  		, Model model
//  		) {
//  	
////  	String username = principal.getName();
////    above replaced by below
//  	String email = principal.getName();
//  	
////  	model.addAttribute("currentUser", userSrv.findByUsername(username));
//  	// above replaced by below
//  	model.addAttribute("currentUser", userSrv.findByEmail(email));
//  	model.addAttribute("userList", userSrv.allUsers());
//  	
//  	return "admin.jsp";
//  }
  
  // JRF: temporarily disabling this 'delete users' program.
  
//  @RequestMapping("/delete/{id}")
//	public String deleteUser(
//			@PathVariable("id") Long userId
//			, HttpSession session
//			, Model model
//			) {	
//		UserMdl userMdl = userSrv.findById(userId);
//		userSrv.deleteUser(userMdl);
//		
//		model.addAttribute("userList", userSrv.allUsers());
//		 
//		return "redirect:/admin";
//	}
    
	// view all profile -- REFACTOR!!!!1
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
	
//	// begin: legacy profile page
//	// display profile page
//	@GetMapping("/profile/{id}")
//	public String showProfile(
//			@PathVariable("id") Long userProfileId
//			, Model model
//			, HttpSession session
//			) {
//		
//    	// log out the unauth vs. deliver the auth user data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long userId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(userId));
//		
//		// grab the entire user object using the url parameter, then deliver to page
//		UserMdl userObj = userSrv.findById(userProfileId);
//		model.addAttribute("userProfile", userObj); 
//		
//		// list of playdates where createdBy_id = userProfileId -- CURRENT AND FUTURE
//		List<PlaydateMdl> userHostedPlaydateListCurrentPlus = playdateSrv.userHostedPlaydateListCurrentPlus(userProfileId);
//		model.addAttribute("userHostedPlaydateListCurrentPlus", userHostedPlaydateListCurrentPlus);
//		
//		// list of playdates where createdBy_id = userProfileId -- PAST
//		List<PlaydateMdl> userHostedPlaydateListPast = playdateSrv.userHostedPlaydateListPast(userProfileId);
//		model.addAttribute("userHostedPlaydateListPast", userHostedPlaydateListPast);
//		
////			Date todayDate = new Date(); // not sure why this here, I think it was for testing; 9/12 
//		return "profile/record.jsp";
//	}
//	// end: legacy profile page
	
	// display profile page
    @GetMapping("/profile/{id}")
    public String showProfile(
    		@PathVariable("id") Long userProfileId
    		, Principal principal
    		, Model model
    		) {    	
         
        String email = principal.getName();
		UserMdl userMdl = userSrv.findByEmail(email);
		model.addAttribute("authUser", userMdl);
		
		// grab the entire user object using the url parameter, then deliver to page
		UserMdl userObj = userSrv.findById(userProfileId);
		model.addAttribute("userProfile", userObj); 

		return "profile/record.jsp";
    }
	
//    // begin: legacy edit profile methods
//	// display edit page
//	@GetMapping("/profile/{id}/edit")
//	public String editProfile(
////				@ModelAttribute("userProfileTobe") UserupdateMdl userupdateObj
//			@ModelAttribute("userProfileTobe") UserUpdateDto userupdateObj
//			, @PathVariable("id") Long userProfileId
//			, Model model
//			, HttpSession session
//			) {
//		
//		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long userId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(userId)); 
//
//		// (1) acquire as-is object/values from db
//		UserMdl userObj = userSrv.findById(userProfileId); 
//		
//		// (2) Populate the initially-empty userupdateObj (being sent to the form) with the values from the existing record
//		userupdateObj.setAboutMe(userObj.getAboutMe()); 
//		userupdateObj.setLastName(userObj.getLastName()); 
//		userupdateObj.setUserName(userObj.getUserName()); 
//		userupdateObj.setEmail(userObj.getEmail()); 
//		userupdateObj.setFirstName(userObj.getFirstName()); 
//		userupdateObj.setCity(userObj.getCity()); 
//		userupdateObj.setZipCode(userObj.getZipCode()); 
//		
//		// (3) not related to above, but easily confused: 
//		
//		model.addAttribute("userProfile", userObj); // send the as-is object to the page, so static values can be used (createdAt, Id, etc.)
//
//		return "profile/edit.jsp";
//	}
//	
//	// process the edit
//	@PostMapping("/profile/edit")
//	public String PostTheEditProfile(
//			@Valid 
////				@ModelAttribute("userProfileTobe") UserupdateMdl userupdateObj
//			@ModelAttribute("userProfileTobe") UserUpdateDto userupdateObj
//			, BindingResult result
//			, Model model
//			, HttpSession session
//			, RedirectAttributes redirectAttributes
//			) {
//		
//		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
//		
//		// (1) acquire as-is object/values from db (as you did in the display mthd)
//		UserMdl userObj = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
//		
//		// (2) overwrite the targeted fields in the userObj with values from the userupdateObj
//		userObj.setEmail(userupdateObj.getEmail()); 
//		userObj.setUserName(userupdateObj.getUserName()); 
//		userObj.setFirstName(userupdateObj.getFirstName() ); 
//		userObj.setLastName(userupdateObj.getLastName() ); 
//		
//		userObj.setAboutMe(userupdateObj.getAboutMe() ); 
//		userObj.setCity(userupdateObj.getCity() );  
//		userObj.setZipCode(userupdateObj.getZipCode() ); 
//		
//		// (3) run the service to save the updated object
//		userSrv.updateUserProfile(userObj, result);
//		
//		if (result.hasErrors() ) { 
//			model.addAttribute("userProfile", userObj); // send the as-is object to the page, so static values can be used (createdAt, Id, etc.)
//			return "profile/edit.jsp";
//		} else {
//			return "redirect:/profile/" + userObj.getId(); 
//		}
//	}
//    // end: legacy edit profile methods
    
	// display edit page
	@GetMapping("/profile/{id}/edit")
	public String editProfile(
//			@ModelAttribute("userProfileTobe") UserupdateMdl userupdateObj
			@ModelAttribute("userProfileTobe") UserUpdateDto userupdateObj
			, @PathVariable("id") Long userProfileId
			, Model model
//			, HttpSession session
			, Principal principal
			) {
		
		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long userId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(userId)); 
		// above replaced by below
		String email = principal.getName();
		UserMdl userMdl = userSrv.findByEmail(email);
		model.addAttribute("authUser", userMdl);

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
//			@ModelAttribute("userProfileTobe") UserupdateMdl userupdateObj
			@ModelAttribute("userProfileTobe") UserUpdateDto userupdateObj
			, BindingResult result
			, Model model
//			, HttpSession session
			, Principal principal
			, RedirectAttributes redirectAttributes
			) {
		
		String email = principal.getName();
		UserMdl userMdl = userSrv.findByEmail(email);
		model.addAttribute("authUser", userMdl);
		
		Long authenticatedUserId = userMdl.getId(); 
		
		// (1) acquire as-is object/values from db (as you did in the display mthd)
		UserMdl userObj = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		
		// (2) overwrite the targeted fields in the userObj with values from the userupdateObj
//		userObj.setEmail(userupdateObj.getEmail()); // this line shall be enabled, once we figure out how to update the authentication object to contain the updated email addy
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
			System.out.println("email: " + email); 
			System.out.println("userMdl: " + userMdl);
			
//			https://stackoverflow.com/questions/14010326/how-to-change-the-login-name-for-the-current-user-with-spring-security-3-1
//			Authentication request = new UsernamePasswordAuthenticationToken(userMdl.getEmail(), password);
//			Authentication resultX = resultX.authenticate(request);
//			SecurityContextHolder.getContext().setAuthentication(resultX);
			
//			https://stackoverflow.com/questions/7889660/how-to-reload-spring-security-principal-after-updating-in-hibernate
//			Authentication authentication = new UsernamePasswordAuthenticationToken(userObject, userObject.getPassword(), userObject.getAuthorities());
//			SecurityContextHolder.getContext().setAuthentication(authentication);

//			return "redirect:/profile/" + userObj.getId(); 
			return "redirect:/"; 
		}
	}
	
// end of methods
}
