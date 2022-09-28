package com.jonfriend.playdatenow_v04.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.jonfriend.playdatenow_v04.models.UserMdl;
import com.jonfriend.playdatenow_v04.pojos.PlaydateUserUnionRsvpUser;
import com.jonfriend.playdatenow_v04.models.RsvpMdl;
import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
import com.jonfriend.playdatenow_v04.services.RsvpSrv;
import com.jonfriend.playdatenow_v04.services.PlaydateSrv;
import com.jonfriend.playdatenow_v04.services.UserSrv;

@Controller
public class PlaydateCtl {

	@Autowired
	private PlaydateSrv playdateSrv;
	
	@Autowired
	private UserSrv userSrv;
	
	@Autowired
	private RsvpSrv rsvpSrv; 
	
	// view all record
	@GetMapping("/playdate")
	public String showAllPlaydate(
			@ModelAttribute("playdate") PlaydateMdl playdateMdl // this needed to display create-new on the page
			, Principal principal // added for spring
			, Model model
//			, HttpSession session // whacked for spring
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
		
		// orig return all list
		List<PlaydateMdl> playdateList = playdateSrv.returnAll();
		model.addAttribute("playdateList", playdateList);
		return "playdate/list.jsp";
	}
	
	// display create-new page
	@GetMapping("/playdate/new")
	public String newPlaydate(
			@ModelAttribute("playdate") PlaydateMdl playdateMdl
			, Model model
//			, HttpSession session
			, Principal principal
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
		
		String[] startTimeList = { "8:00am",	"8:30am",	"9:00am",	"9:30am",	"10:00am",	"10:30am",	"11:00am",	"11:30am",	"12:00pm",	"12:30pm",	"1:00pm",	"1:30pm",	"2:00pm",	"2:30pm",	"3:00pm",	"3:30pm",	"4:00pm",	"4:30pm",	"5:00pm",	"5:30pm",	"6:00pm",	"6:30pm",	"7:00pm",	"7:30pm",	"8:00pm",	"8:30pm"};
		model.addAttribute("startTimeList", startTimeList ); 
		return "playdate/create.jsp";
	}
	 
	// process the create-new  
	@PostMapping("/playdate/new")
	public String addNewPlaydate(
			@Valid @ModelAttribute("playdate") PlaydateMdl playdateMdl
			, BindingResult result
			, Model model
//			, HttpSession session
			, Principal principal
			, RedirectAttributes redirectAttributes
			) {
		
		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
		if(result.hasErrors()) {
			model.addAttribute("validationErrorMsg", "Uh-oh! Please fix the errors noted below and submit again.  (Or cancel.)"); 
			String[] startTimeList = { "8:00am",	"8:30am",	"9:00am",	"9:30am",	"10:00am",	"10:30am",	"11:00am",	"11:30am",	"12:00pm",	"12:30pm",	"1:00pm",	"1:30pm",	"2:00pm",	"2:30pm",	"3:00pm",	"3:30pm",	"4:00pm",	"4:30pm",	"5:00pm",	"5:30pm",	"6:00pm",	"6:30pm",	"7:00pm",	"7:30pm",	"8:00pm",	"8:30pm"};
			model.addAttribute("startTimeList", startTimeList ); 
			return "playdate/create.jsp";
		
		} else {

//			UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); // gets the userModel object by calling the user service with the session user id
//			playdateMdl.setUserMdl( currentUserMdl); //  sets the userId of the new record with above acquisition.
			// above two lines replaced with below!
			playdateMdl.setUserMdl( authUserObj); 
			playdateSrv.create(playdateMdl);
			Long newlyCreatedPlaydateID = playdateMdl.getId();  
			redirectAttributes.addFlashAttribute("successMsg", "This playdate is gonna be awesome!  Make sure you invite some friends to RSVP.");
			return "redirect:/playdate/" + newlyCreatedPlaydateID;
		}
	}
	
	// view record
	@GetMapping("/playdate/{id}")
	public String showPlaydate(
			@PathVariable("id") Long playdateId
			, @ModelAttribute("rsvp") RsvpMdl rsvpMdl // enables deliver of a RSVP record on the page
			, Model model
//			, HttpSession session
			, Principal principal
			) {
		
		// log out the unauth / deliver the auth user data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); // used for show/orNot edit playdate button
		// above line no longer needed
		PlaydateMdl playdateObj = playdateSrv.findById(playdateId); // display native playdate fields, etc.
		
		// begin: calculate various RSVP-related stats.  NOTE: this all could be done with native queries as well, but this is good function/loop practice.  
		List<RsvpMdl> rsvpList = rsvpSrv.returnAllRsvpForPlaydate(playdateObj); // list of rsvps, which we will use downstream
		Integer rsvpCount = 0; 							// instantiate the java variable that we will update in the loop
		Integer aggKidsCount = 0; 						// instantiate the java variable that we will update in the loop 
		Integer aggAdultsCount = 0; 					// instantiate the java variable that we will update in the loop
		Boolean rsvpExistsCreatedByAuthUser = false; 	// instantiate the java variable that we will update in the loop
		Integer openKidsSpots = 0; 						// instantiate the java variable that we will update in the loop
		
		for (int i=0; i < rsvpList.size(); i++  ) { 	// for each record in the list of RSVPs... 
			if ( rsvpList.get(i).getRsvpStatus().equals("In")) { // we only count the 'in' records towards totals
				rsvpCount += 1;  
				aggKidsCount += rsvpList.get(i).getKidCount();
				aggAdultsCount += rsvpList.get(i).getAdultCount(); 
			} 
//			if (rsvpList.get(i).getUserMdl().equals(currentUserMdl) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
			// above replaced by below	
			if (rsvpList.get(i).getUserMdl().equals(authUserObj) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
			{
				rsvpExistsCreatedByAuthUser = true;  
				RsvpMdl rsvpObjForAuthUser = rsvpSrv.findById(rsvpList.get(i).getId()); 
				model.addAttribute("rsvpObjForAuthUser", rsvpObjForAuthUser); 
			}   
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
		model.addAttribute("rsvpExistsCreatedByAuthUser", rsvpExistsCreatedByAuthUser);
		model.addAttribute("rsvpList", rsvpList);
		model.addAttribute("aggAdultsCount", aggAdultsCount); 
		model.addAttribute("openKidsSpots", openKidsSpots); 
		// end: calculate various RSVP-related stats.  
		
		// begin: get/deliver list of unioned rsvp records
		List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
		model.addAttribute("playdateRsvpList", playdateRsvpList); // end: get/deliver list of unioned rsvp records  

		return "playdate/record.jsp";
	}

	// display edit page
	@GetMapping("/playdate/{id}/edit")
	public String editPlaydate(
			@PathVariable("id") Long playdateId
			, @ModelAttribute("rsvp") RsvpMdl rsvpMdl // added, trying to put RSVP form on page
			, Model model
//			, HttpSession session
			, Principal principal
			) {
		
		System.out.println("on edit display mthd"); 
//		// log out the unauth / deliver the auth user data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); // used for show/orNot edit playdate button; gets the userModel object by calling the user service with the session user id value
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
		PlaydateMdl playdateObj = playdateSrv.findById(playdateId); // display native playdate fields, etc.
		model.addAttribute("playdate", playdateObj);
		
		// get/deliver from playdate db record, Part 1 (so they can be addAtts that are independent from the playdate obj)
		Date playdateCreatedAt = playdateObj.getCreatedAt(); 
		Long playdateCreatedById = playdateObj.getUserMdl().getId();
		String playdateCreatedByUserName = playdateObj.getUserMdl().getUserName();
		
		// begin: calculate various RSVP-related stats.  NOTE: this all could be done with native queries as well, but this is good function/loop practice.  
		List<RsvpMdl> rsvpList = rsvpSrv.returnAllRsvpForPlaydate(playdateObj); // list of rsvps, which we will use downstream
		Integer rsvpCount = 0; 							// instantiate the java variable that we will update in the loop
		Integer aggKidsCount = 0; 						// instantiate the java variable that we will update in the loop 
		Integer aggAdultsCount = 0; 					// instantiate the java variable that we will update in the loop
		Boolean rsvpExistsCreatedByAuthUser = false; 	// instantiate the java variable that we will update in the loop
		Integer openKidsSpots = 0; 						// instantiate the java variable that we will update in the loop
		
		for (int i=0; i < rsvpList.size(); i++  ) { 	// for each record in the list of RSVPs... 
			if ( rsvpList.get(i).getRsvpStatus().equals("In")) { // we only count the 'in' records towards totals
				rsvpCount += 1;  
				aggKidsCount += rsvpList.get(i).getKidCount();
				aggAdultsCount += rsvpList.get(i).getAdultCount(); 
			} 
//			if (rsvpList.get(i).getUserMdl().equals(currentUserMdl) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
			// above replaced by below
			if (rsvpList.get(i).getUserMdl().equals(authUserObj) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
			{
				rsvpExistsCreatedByAuthUser = true;  
				RsvpMdl rsvpObjForAuthUser = rsvpSrv.findById(rsvpList.get(i).getId()); 
				model.addAttribute("rsvpObjForAuthUser", rsvpObjForAuthUser); 
			}   
		}
		
		if (playdateObj.getRsvpStatus().equals("In")) { // this 'if' accounts for the host family
			rsvpCount += 1;  
			aggKidsCount += playdateObj.getKidCount(); 
			aggAdultsCount += playdateObj.getAdultCount(); 				
		}
		
		openKidsSpots = playdateObj.getMaxCountKids() - aggKidsCount; 

		model.addAttribute("rsvpCount", rsvpCount); 
		model.addAttribute("aggKidsCount", aggKidsCount); 
		model.addAttribute("rsvpExistsCreatedByAuthUser", rsvpExistsCreatedByAuthUser); // this is not relevant for edit screen, b/c authuser is host, and has no RSVP ever, but leave it
		model.addAttribute("rsvpList", rsvpList);
		model.addAttribute("aggAdultsCount", aggAdultsCount); 
		model.addAttribute("openKidsSpots", openKidsSpots); 
		// end: calculate various RSVP-related stats.  
		
		// get/deliver list of unioned rsvp records
		List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
		model.addAttribute("playdateRsvpList", playdateRsvpList);
		
		Boolean hasOneOrMoreRsvp = false; // at present, this is being used to show the delete button or not. 
		if ( rsvpList.size() > 0 ) {
			hasOneOrMoreRsvp = true;
		}
		model.addAttribute("hasOneOrMoreRsvp", hasOneOrMoreRsvp);
		
		String[] startTimeList = { "8:00am",	"8:30am",	"9:00am",	"9:30am",	"10:00am",	"10:30am",	"11:00am",	"11:30am",	"12:00pm",	"12:30pm",	"1:00pm",	"1:30pm",	"2:00pm",	"2:30pm",	"3:00pm",	"3:30pm",	"4:00pm",	"4:30pm",	"5:00pm",	"5:30pm",	"6:00pm",	"6:30pm",	"7:00pm",	"7:30pm",	"8:00pm",	"8:30pm"};
		model.addAttribute("startTimeList", startTimeList ); 
		
		// get/deliver from playdate db record, Part 2 (so they can be addAtts that are independent from the playdate obj)
		model.addAttribute("playdateCreatedAt", playdateCreatedAt); 
		model.addAttribute("playdateCreatedById", playdateCreatedById);
		model.addAttribute("playdateCreatedByUserName", playdateCreatedByUserName);
		
		return "playdate/edit.jsp";
	}
	
	// process the edit(s)
	@PostMapping("/playdate/edit")
	public String PostTheEditPlaydate(
			@Valid 
			@ModelAttribute("playdate") PlaydateMdl playdateMdl 
			, BindingResult result
			, Model model
//			, HttpSession session
			, Principal principal
			, RedirectAttributes redirectAttributes
			) {
		
		System.out.println("on edit process mthd"); 
		
//		// log out the unauth / deliver the auth use data
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
		model.addAttribute("authUser", authUserObj);
		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
		PlaydateMdl playdateObj = playdateSrv.findById(playdateMdl.getId()); // sets up playdate object by using the getID on the modAtt thing.
		
		// get/deliver from playdate db record, Part 1 (so they can be addAtts that are independent from the playdate obj)
		Date playdateCreatedAt = playdateObj.getCreatedAt(); 
		Long playdateCreatedById = playdateObj.getUserMdl().getId(); 
		String playdateCreatedByUserName = playdateObj.getUserMdl().getUserName();
		
		// validate if user = creator
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		// above line no longer needed
		UserMdl recordCreatorUserMdl = playdateObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj 
//		if(!currentUserMdl.equals(recordCreatorUserMdl)) {
		// above replaced by below
		if(!authUserObj.equals(recordCreatorUserMdl)) {
			System.out.println("recordCreatorUserMdl != currentUserMdl, so redirected to record"); 
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "This record can only be edited by its creator.  Any edits just attempted were discarded.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
		
		if (result.hasErrors()) { 			
			// note: do not re-deliver playdateObj: doing so will overrride all the values the users submitted / system rejected
			model.addAttribute("validationErrorMsg", "Uh-oh! Please fix the errors noted below and submit again.  (Or cancel.)"); //redirectAttributes doesn't work here b/c we are not redirecting, we are merely returning.  so use modAtt instead.
			Long playdateId = playdateMdl.getId(); // need this here, so that the playdateId can be referenced downstream
			
			// begin: calculate various RSVP-related stats.  NOTE: this all could be done with native queries as well, but this is good function/loop practice.  
			List<RsvpMdl> rsvpList = rsvpSrv.returnAllRsvpForPlaydate(playdateObj); // list of rsvps, which we will use downstream
			Integer rsvpCount = 0; 							// instantiate the java variable that we will update in the loop
			Integer aggKidsCount = 0; 						// instantiate the java variable that we will update in the loop 
			Integer aggAdultsCount = 0; 					// instantiate the java variable that we will update in the loop
			Boolean rsvpExistsCreatedByAuthUser = false; 	// instantiate the java variable that we will update in the loop
			Integer openKidsSpots = 0; 						// instantiate the java variable that we will update in the loop
			
			for (int i=0; i < rsvpList.size(); i++  ) { 	// for each record in the list of RSVPs... 
				if ( rsvpList.get(i).getRsvpStatus().equals("In")) { // we only count the 'in' records towards totals
					rsvpCount += 1;  
					aggKidsCount += rsvpList.get(i).getKidCount();
					aggAdultsCount += rsvpList.get(i).getAdultCount(); 
				} 
//				if (rsvpList.get(i).getUserMdl().equals(currentUserMdl) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
				// above replaced by below
				if (rsvpList.get(i).getUserMdl().equals(authUserObj) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
				{
					rsvpExistsCreatedByAuthUser = true;  
					RsvpMdl rsvpObjForAuthUser = rsvpSrv.findById(rsvpList.get(i).getId()); 
					model.addAttribute("rsvpObjForAuthUser", rsvpObjForAuthUser); 
				}   
			}
			
			if (playdateObj.getRsvpStatus().equals("In")) { // this 'if' accounts for the host family
				rsvpCount += 1;  
				aggKidsCount += playdateObj.getKidCount(); 
				aggAdultsCount += playdateObj.getAdultCount(); 				
			}
			
			openKidsSpots = playdateObj.getMaxCountKids() - aggKidsCount; 

			model.addAttribute("rsvpCount", rsvpCount); 
			model.addAttribute("aggKidsCount", aggKidsCount); 
			model.addAttribute("rsvpExistsCreatedByAuthUser", rsvpExistsCreatedByAuthUser); // this is not relevant for edit screen, b/c authuser is host, and has no RSVP ever, but leave it
			model.addAttribute("rsvpList", rsvpList);
			model.addAttribute("aggAdultsCount", aggAdultsCount); 
			model.addAttribute("openKidsSpots", openKidsSpots); 
			// end: calculate various RSVP-related stats.  
			
			// get/deliver list of unioned rsvp records
			List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
			model.addAttribute("playdateRsvpList", playdateRsvpList);
			
			Boolean hasOneOrMoreRsvp = false; // at present, this is being used to show the delete button or not. 
			if ( rsvpList.size() > 0 ) {
				hasOneOrMoreRsvp = true;
			}
			model.addAttribute("hasOneOrMoreRsvp", hasOneOrMoreRsvp);
			
			String[] startTimeList = { "8:00am",	"8:30am",	"9:00am",	"9:30am",	"10:00am",	"10:30am",	"11:00am",	"11:30am",	"12:00pm",	"12:30pm",	"1:00pm",	"1:30pm",	"2:00pm",	"2:30pm",	"3:00pm",	"3:30pm",	"4:00pm",	"4:30pm",	"5:00pm",	"5:30pm",	"6:00pm",	"6:30pm",	"7:00pm",	"7:30pm",	"8:00pm",	"8:30pm"};
			model.addAttribute("startTimeList", startTimeList ); 
			
			// get/deliver from playdate db record, Part 2 (so they can be addAtts that are independent from the playdate obj)
			model.addAttribute("playdateCreatedAt", playdateCreatedAt); 
			model.addAttribute("playdateCreatedById", playdateCreatedById);
			model.addAttribute("playdateCreatedByUserName", playdateCreatedByUserName);
			
			return "playdate/edit.jsp";
			
		} else {
			playdateMdl.setUserMdl(playdateObj.getUserMdl()); // deliver any attribute of the object/record not managed by the form
			playdateSrv.update(playdateMdl);
			return "redirect:/playdate/" + playdateObj.getId();
		}
	}
	
	// delete playdate
//    @DeleteMapping("/playdate/{id}") 
	// sept27: above replaced by below
    @RequestMapping("/playdate/delete/{id}") 
    public String deletePlaydate(
    		@PathVariable("id") Long playdateId
//    		, HttpSession session
			, Principal principal
    		, RedirectAttributes redirectAttributes
    		) {

    	//		// If no userId is found in session, redirect to logout.  JRF: put this on basically all methods now, except the login/reg pages
//		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
//		Long authenticatedUserId = (Long) session.getAttribute("userId");
//		// model.addAttribute("authUser", userSrv.findById(AuthenticatedUserId));
    	
		// above replaced by below
    	// authentication boilerplate for all mthd
		UserMdl authUserObj = userSrv.findByEmail(principal.getName());
//		model.addAttribute("authUser", authUserObj);
//		model.addAttribute("authUserName", authUserObj.getUserName()); // set the "as-is" username, so it can be statically posted to the top right nav bar
		
		PlaydateMdl playdateObj = playdateSrv.findById(playdateId);
		
//		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		// above line no longer needed
		UserMdl recordCreatorUserMdl = playdateObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj		
		
		if(!authUserObj.equals(recordCreatorUserMdl)) {
			System.out.println("recordCreatorUserMdl != currentUserMdl, so redirected to record"); 
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "This record can only be deleted by its creator.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
		
		List<RsvpMdl> rsvpList = playdateObj.getRsvpList(); // instantiate the java list	
		
		if ( rsvpList.size() > 0 ) {
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "This event has rsvp records, so it cannot be deleted.  If all user RSVPs get deleted, you can then delete this event.  Event no longer happening?  Then update the playdateStatus to be Cancelled.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
			
		playdateSrv.delete(playdateObj);
		
        return "redirect:/playdate";
    }
	
// end of methods
}
