package com.jonfriend.playdatenow_v04.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.jonfriend.playdatenow_v04.models.RsvpMdl;
import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
import com.jonfriend.playdatenow_v04.models.UserMdl;
import com.jonfriend.playdatenow_v04.pojos.PlaydateUserUnionRsvpUser;
import com.jonfriend.playdatenow_v04.services.RsvpSrv;
import com.jonfriend.playdatenow_v04.services.PlaydateSrv;
import com.jonfriend.playdatenow_v04.services.UserSrv;

@Controller
//public class RsvpCtl {
public class RsvpCtl {

	@Autowired
	private RsvpSrv rsvpSrv;
	
	@Autowired
	private UserSrv userSrv;
	
	@Autowired
	private PlaydateSrv playdateSrv;
	
	// process the create-new  
	@PostMapping("/playdate/{id}/rsvp/create")
	public String addNewRsvp(
			@PathVariable ("id") Long playdateId
			, @Valid @ModelAttribute("rsvp") RsvpMdl rsvpMdl
			, BindingResult result
			, Model model
//			, HttpSession session
			, Principal principal // added for spring
			) {
		
//		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
		PlaydateMdl playdateObj = playdateSrv.findById(playdateId);  // gets us the playdate object by using incoming path variable
		
		if(result.hasErrors()) {
			model.addAttribute("playdate", playdateObj);  // sending the parent record to the page
			return "playdate/record.jsp";
		} else {
			
			// note: JRF not sure (2022-09-13) why this approach (using 'newOtc' object/etc.) is necessary here, but I do know from earlier testing that the create/validate/etc. won't workw with the reg approach
			// first... get current user whole object, for infusion into rsvp record
//			UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); 
			// line above no longer needed with spring security
			
			// next, instantiate the new object
			RsvpMdl newOtc = new RsvpMdl(); // ... and then: infuse into that object all the values from the incoming model/form
			newOtc.setPlaydateMdl(playdateObj); // parent record
//			newOtc.setUserMdl(currentUserMdl); // user that is creating it
			// above replaced by below
			newOtc.setUserMdl(authUserObj); // user that is creating it
			newOtc.setRsvpStatus(rsvpMdl.getRsvpStatus()); 
			newOtc.setKidCount(rsvpMdl.getKidCount()); 
			newOtc.setAdultCount(rsvpMdl.getAdultCount()); 
			newOtc.setComment(rsvpMdl.getComment());

			rsvpSrv.create(newOtc);
			
			return "redirect:/playdate/" + playdateId ;
		}
	} 

	// display edit page
	@GetMapping("playdate/{playdateId}/rsvp/{rsvpId}/edit")
	public String editRsvp(
			@PathVariable("playdateId") Long playdateId
			, @PathVariable("rsvpId") Long rsvpId
			, Model model
//			, HttpSession session
			, Principal principal // added for spring
			) {
		
//		// log out the unauth / deliver the auth user data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
		RsvpMdl rsvpObj = rsvpSrv.findById(rsvpId); // get the object that is the primary object displayed on this page
		model.addAttribute("rsvp", rsvpObj); // deliver the object that is the primary object on this page
		PlaydateMdl playdateObj = rsvpObj.getPlaydateMdl(); // get the object that is the parent to the primary object, for all downstream functions
		
		// get/deliver from playdate db record, Part 1 (so they can be addAtts that are independent from the playdate obj)
		Date playdateCreatedAt = playdateObj.getCreatedAt(); 
		Long playdateCreatedById = playdateObj.getUserMdl().getId();
		String playdateCreatedByUserName = playdateObj.getUserMdl().getUserName();
		
		// begin: calculate various RSVP-related stats.  NOTE: this all could be done with native queries as well, but this is good function/loop practice.  
		List<RsvpMdl> rsvpList = rsvpSrv.returnAllRsvpForPlaydate(playdateObj); // list of rsvps, which we will use downstream
		Integer rsvpCount = 0; 							// instantiate the java variable that we will update in the loop
		Integer aggKidsCount = 0; 						// instantiate the java variable that we will update in the loop 
		Integer aggAdultsCount = 0; 					// instantiate the java variable that we will update in the loop
//		Boolean rsvpExistsCreatedByAuthUser = false; 	// not needed; inherent with the method
		Integer openKidsSpots = 0; 						// instantiate the java variable that we will update in the loop
		
		for (int i=0; i < rsvpList.size(); i++  ) { 	// for each record in the list of RSVPs... 
			if ( rsvpList.get(i).getRsvpStatus().equals("In")) { // we only count the 'in' records towards totals
				rsvpCount += 1;  
				aggKidsCount += rsvpList.get(i).getKidCount();
				aggAdultsCount += rsvpList.get(i).getAdultCount(); 
			} 
			// in constrast to playdate controllers, no 'if' clause needed for tagging rsvp record to be displayed; 
		}
		
		if (playdateObj.getRsvpStatus().equals("In")) { // this 'if' accounts for the host family
			rsvpCount += 1;  
			aggKidsCount += playdateObj.getKidCount(); 
			aggAdultsCount += playdateObj.getAdultCount(); 				
		}
		
		openKidsSpots = playdateObj.getMaxCountKids() - aggKidsCount; 

		model.addAttribute("playdate", playdateObj);
		model.addAttribute("rsvpCount", rsvpCount); 
		model.addAttribute("aggKidsCount", aggKidsCount); 
		model.addAttribute("rsvpList", rsvpList);
		model.addAttribute("aggAdultsCount", aggAdultsCount); 
		model.addAttribute("openKidsSpots", openKidsSpots); 
		// end: calculate various RSVP-related stats.  
		
		// get/deliver list of unioned rsvp records
		List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
		model.addAttribute("playdateRsvpList", playdateRsvpList);  
		
		// get/deliver from playdate db record, Part 2 (so they can be addAtts that are independent from the playdate obj)
		model.addAttribute("playdateCreatedAt", playdateCreatedAt); 
		model.addAttribute("playdateCreatedById", playdateCreatedById);
		model.addAttribute("playdateCreatedByUserName", playdateCreatedByUserName);

		return "rsvp/edit.jsp";		
	}
	
	// process the edit
	@PostMapping("/rsvp/edit")
	public String PostTheEditRsvp(
//			@PathVariable ("id") Long playdateId
			@Valid @ModelAttribute("rsvp") RsvpMdl rsvpMdl
			, BindingResult result
			, Model model
//			, HttpSession session
			, Principal principal // added for spring
			, RedirectAttributes redirectAttributes
			) {
		
		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		 
		RsvpMdl rsvpObj = rsvpSrv.findById(rsvpMdl.getId()); // below now setting up rthe rsvp object by using the getID on the modAtt thing.
		PlaydateMdl playdateObj = rsvpObj.getPlaydateMdl(); // get the object that is the parent to the primary object
		
		// get/deliver from playdate db record, Part 1 (so they can be addAtts that are independent from the playdate obj)
		Date playdateCreatedAt = playdateObj.getCreatedAt(); 
		Long playdateCreatedById = playdateObj.getUserMdl().getId();
		String playdateCreatedByUserName = playdateObj.getUserMdl().getUserName();
		Long playdateId = playdateObj.getId(); 
		
		UserMdl rsvpCreatorUserMdl = rsvpObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj
		
		if(!authUserObj.equals(rsvpCreatorUserMdl)) {
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "That RSVP can only be edited by its creator.  Any edits just attempted have been discarded.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
		
		if(result.hasErrors()) { // examples of errors: submit with null kidsCount, etc.
			// begin: calculate various RSVP-related stats.  NOTE: this all could be done with native queries as well, but this is good function/loop practice.  
			List<RsvpMdl> rsvpList = rsvpSrv.returnAllRsvpForPlaydate(playdateObj); // list of rsvps, which we will use downstream
			Integer rsvpCount = 0; 							// instantiate the java variable that we will update in the loop
			Integer aggKidsCount = 0; 						// instantiate the java variable that we will update in the loop 
			Integer aggAdultsCount = 0; 					// instantiate the java variable that we will update in the loop
//			Boolean rsvpExistsCreatedByAuthUser = false; 	// not needed; inherent with the method
			Integer openKidsSpots = 0; 						// instantiate the java variable that we will update in the loop
			
			for (int i=0; i < rsvpList.size(); i++  ) { 	// for each record in the list of RSVPs... 
				if ( rsvpList.get(i).getRsvpStatus().equals("In")) { // we only count the 'in' records towards totals
					rsvpCount += 1;  
					aggKidsCount += rsvpList.get(i).getKidCount();
					aggAdultsCount += rsvpList.get(i).getAdultCount(); 
				} 
				// in constrast to playdate controllers, no 'if' clause needed for tagging rsvp record to be displayed; 
			}
			
			if (playdateObj.getRsvpStatus().equals("In")) { // this 'if' accounts for the host family
				rsvpCount += 1;  
				aggKidsCount += playdateObj.getKidCount(); 
				aggAdultsCount += playdateObj.getAdultCount(); 				
			}
			
			openKidsSpots = playdateObj.getMaxCountKids() - aggKidsCount; 

			model.addAttribute("playdate", playdateObj);
			model.addAttribute("rsvpCount", rsvpCount); 
			model.addAttribute("aggKidsCount", aggKidsCount); 
			model.addAttribute("rsvpList", rsvpList);
			model.addAttribute("aggAdultsCount", aggAdultsCount); 
			model.addAttribute("openKidsSpots", openKidsSpots); 
			// end: calculate various RSVP-related stats.  
			
			// get/deliver list of unioned rsvp records
			List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
			model.addAttribute("playdateRsvpList", playdateRsvpList);  
			
			// get/deliver from playdate db record, Part 2 (so they can be addAtts that are independent from the playdate obj)
			model.addAttribute("playdateCreatedAt", playdateCreatedAt); 
			model.addAttribute("playdateCreatedById", playdateCreatedById);
			model.addAttribute("playdateCreatedByUserName", playdateCreatedByUserName);
			return "rsvp/edit.jsp"; // not sure if/how it is advisable to have this return/redirect/whatev to the url that contains the playdate_id and rsvp_id
			
		} else {
			UserMdl origCreatorUserMdl = rsvpObj.getUserMdl();  // get the user object that created it in the first place, that's what we want to maintain
			rsvpMdl.setUserMdl( origCreatorUserMdl);
			rsvpMdl.setPlaydateMdl(playdateObj); 
			rsvpSrv.update(rsvpMdl);

			return "redirect:/playdate/" + playdateId;
		}
	} 
	
	// below is delete mthd before spring security; frozen. 
//	// delete rsvp
//    @DeleteMapping("/rsvp/{id}")
//    public String deleteRsvp(
//    		@PathVariable("id") Long rsvpId
//    		, HttpSession session
//    		, RedirectAttributes redirectAttributes
//    		) {
//		// If no userId is found in session, redirect to logout.  JRF: put this on basically all methods now, except the login/reg pages
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//
//		RsvpMdl rsvpObj = rsvpSrv.findById(rsvpId);
//		PlaydateMdl playdateObj = rsvpObj.getPlaydateMdl(); 
//		Long playdateID = playdateObj.getId(); 
//		
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
//		UserMdl rsvpCreatorUserMdl = rsvpObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj
//		
//		if(!currentUserMdl.equals(rsvpCreatorUserMdl)) {
//			redirectAttributes.addFlashAttribute("permissionErrorMsg", "That RSVP can only be deleted by its creator.  RSVP not deleted.");
//			return "redirect:/playdate/" + playdateObj.getId();
//		}
//
//		rsvpSrv.delete(rsvpObj);
//        return "redirect:/playdate/" + playdateID;
//    }
	
	// below is the deleteMapping method which should work.... but won't.  replaced by wholly new approach that follows
//	// delete rsvp
//    @DeleteMapping("/rsvp/{id}")
//    public String deleteRsvp(
//    		@PathVariable("id") Long rsvpId
////    		, HttpSession session
//			, Principal principal // added for spring
//    		, RedirectAttributes redirectAttributes
//    		) {
////		// If no userId is found in session, redirect to logout.  JRF: put this on basically all methods now, except the login/reg pages
////		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
////		Long authenticatedUserId = (Long) session.getAttribute("userId");
//    	
//		// above replaced by below
//    	// authentication boilerplate for all mthd
//		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
////		model.addAttribute("authUser", authUserObj);
////		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
//
//		RsvpMdl rsvpObj = rsvpSrv.findById(rsvpId);
//		PlaydateMdl playdateObj = rsvpObj.getPlaydateMdl(); 
//		Long playdateID = playdateObj.getId(); 
//		
////		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
//		// above no longer needed
//		UserMdl rsvpCreatorUserMdl = rsvpObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj
//		
////		if(!currentUserMdl.equals(rsvpCreatorUserMdl)) {
//		// above replaced by below
//		if(!authUserObj.equals(rsvpCreatorUserMdl)) {
//			redirectAttributes.addFlashAttribute("permissionErrorMsg", "That RSVP can only be deleted by its creator.  RSVP not deleted.");
//			return "redirect:/playdate/" + playdateObj.getId();
//		}
//
//		rsvpSrv.delete(rsvpObj);
//        return "redirect:/playdate/" + playdateID;
//    }
	
	// delete rsvp
//    @DeleteMapping("/rsvp/{id}")
	@RequestMapping("/rsvp/delete/{id}")
    public String deleteRsvp(
    		@PathVariable("id") Long rsvpId
//    		, HttpSession session
			, Principal principal // added for spring
    		, RedirectAttributes redirectAttributes
    		) {
//		// If no userId is found in session, redirect to logout.  JRF: put this on basically all methods now, except the login/reg pages
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
    	
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
//		model.addAttribute("authUser", authUserObj);
//		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar

		RsvpMdl rsvpObj = rsvpSrv.findById(rsvpId);
		PlaydateMdl playdateObj = rsvpObj.getPlaydateMdl(); 
		Long playdateID = playdateObj.getId(); 
		
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		// above no longer needed
		UserMdl rsvpCreatorUserMdl = rsvpObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj
		
//		if(!currentUserMdl.equals(rsvpCreatorUserMdl)) {
		// above replaced by below
		if(!authUserObj.equals(rsvpCreatorUserMdl)) {
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "That RSVP can only be deleted by its creator.  RSVP not deleted.");
			return "redirect:/playdate/" + playdateObj.getId();
		}

		rsvpSrv.delete(rsvpObj);
        return "redirect:/playdate/" + playdateID;
    }
    
// end of methods
}
