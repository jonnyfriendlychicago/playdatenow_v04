package com.jonfriend.playdatenow_v04.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
			, Model model
			, HttpSession session
//			, @Param("keyword") Long keyword // new
			) {
		
		// log out the unauth / deliver the auth use data
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		
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
			, HttpSession session
			) {
		 
		// log out the unauth / deliver the auth use data
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		model.addAttribute("authUser", userSrv.findById(authenticatedUserId)); 
		
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
			, HttpSession session
			, RedirectAttributes redirectAttributes
			) {
		
		// log out the unauth / deliver the auth use data
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		
		if(result.hasErrors()) {
			model.addAttribute("validationErrorMsg", "Uh-oh! Please fix the errors noted below and submit again.  (Or cancel.)"); 
			String[] startTimeList = { "8:00am",	"8:30am",	"9:00am",	"9:30am",	"10:00am",	"10:30am",	"11:00am",	"11:30am",	"12:00pm",	"12:30pm",	"1:00pm",	"1:30pm",	"2:00pm",	"2:30pm",	"3:00pm",	"3:30pm",	"4:00pm",	"4:30pm",	"5:00pm",	"5:30pm",	"6:00pm",	"6:30pm",	"7:00pm",	"7:30pm",	"8:00pm",	"8:30pm"};
			model.addAttribute("startTimeList", startTimeList ); 
			return "playdate/create.jsp";
		} else {

			UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); // gets the userModel object by calling the user service with the session user id
			playdateMdl.setUserMdl( currentUserMdl); //  sets the userId of the new record with above acquisition.
			
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
			, HttpSession session
			) {
		
		// log out the unauth / deliver the auth user data
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		
		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); // used for show/orNot edit playdate button
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
			if (rsvpList.get(i).getUserMdl().equals(currentUserMdl) )  // this 'if' sets needed flags if it exists for the logged in user, and delivers the RSVP object to the page as well
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
		
		// get/deliver list of unioned rsvp records
		List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
		model.addAttribute("playdateRsvpList", playdateRsvpList);  

		
		// START: fun with making lists for page consumption
		
		// quick/dirty stateList
		String[] stateList = { "Alabama", "Alaska", "Arizona"};
		model.addAttribute("stateList", stateList ); 
		
		// initalize empty array list (that we'll populate then send to page)
//		ArrayList<Object> stateListEnhanced = new ArrayList<Object>();
		List<Object> stateListEnhanced = new ArrayList<Object>();
		
		// initialize feeder lists: 
		String[] stateSpellList = {"Alabama" , "Alaska" , "Arizona"}; 
		String[] stateAbbrvList = {"AL" , "AK" , "AZ"}; 
		
		// get variable for submitting to the loop as upper boundary
		Integer stateListSpellDrawerCount =  stateSpellList.length; 
		 
		// initialize for-loop 
		
		for (int i=0; i < stateListSpellDrawerCount; i++  ) {
//				System.out.println("value of item in drawer " + i + " -- " + stateSpellList[i]);
			HashMap<String, String> singleStateHashMap = new HashMap<String, String>();
			singleStateHashMap.put("'StateName'", "'" + stateSpellList[i] + "'");
			singleStateHashMap.put("'StateAbbv'", "'" + stateAbbrvList[i] + "'");
			stateListEnhanced.add(singleStateHashMap); 
		}
		
		System.out.println("stateListEnhanced: " + stateListEnhanced); 
		model.addAttribute("stateListEnhanced", stateListEnhanced ); 
		
		// END: fun with making lists for page consumption
		return "playdate/record.jsp";
	}

	// display edit page
	@GetMapping("/playdate/{id}/edit")
	public String editPlaydate(
			@PathVariable("id") Long playdateId
			, @ModelAttribute("rsvp") RsvpMdl rsvpMdl // added, trying to put RSVP form on page
			, Model model
			, HttpSession session
			) {
		
		// log out the unauth / deliver the auth user data
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		
		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); // gets the userModel object by calling the user service with the session user id value
		PlaydateMdl playdateObj = playdateSrv.findById(playdateId); // pre-populates the values in the playdate edit interface (?).  also, used to get the rsvp list
		List<RsvpMdl> rsvpList = playdateObj.getRsvpList(); // instantiate the java list	
		
//		UserMdl recordCreatorUserMdl = playdateObj.getUserMdl(); // dont' think this is actually being used on this page. 

		// start: fun with functions & loops
			Integer rsvpCount = rsvpList.size(); // get a count of how many items in the list
			Integer aggKidsCount = 0; // instantiate the java variable that we will update in the loop 
			Boolean rsvpExistsCreatedByAuthUser = false; // instantiate the java variable that we will update in the loop
			
			Long rsvpIdCreatedByAuthUser = (long) 0; 
			
			Integer aggAdultsCount = 0; // instantiate the java variable that we will update in the loop
			Integer openKidsSpots = 0; // instantiate the java variable that we will update in the loop
			
			for (int i=0; i < rsvpCount; i++  ) {
				System.out.println("RSVP #" + i +  " (" + rsvpList.get(i).getUserMdl().getUserName() + "): " + rsvpList.get(i).getKidCount() + " (" + rsvpList.get(i).getRsvpStatus() + ")"  ); 
				
	//			aggKidsCount += rsvpList.get(i).getKidCount();
				// JRF: line above replaced by if/else below.   let's see how it goes
				if ( rsvpList.get(i).getRsvpStatus().equals("In")) {
					aggKidsCount = aggKidsCount + rsvpList.get(i).getKidCount(); 
				} else {
					aggKidsCount = aggKidsCount + 0; 
				}
				
				if ( rsvpList.get(i).getRsvpStatus().equals("In")) {
					aggAdultsCount = aggAdultsCount + rsvpList.get(i).getAdultCount(); 
				} else {
					aggAdultsCount = aggAdultsCount + 0; 
				} 
				
				if (rsvpList.get(i).getUserMdl().equals(currentUserMdl) )
				{
					rsvpExistsCreatedByAuthUser = true; // if there's a a match, set to true.  that's it, that's all you gotta do. 
					rsvpIdCreatedByAuthUser = rsvpList.get(i).getId(); 
				}   
			}
			
			openKidsSpots = playdateObj.getMaxCountKids() - aggKidsCount; 
			
			RsvpMdl rsvpObjForAuthUser = rsvpSrv.findById(rsvpIdCreatedByAuthUser); 
		// end: fun with functions & loops
		
		Boolean hasOneOrMoreRsvp = false; // at present, this is being used to show the delete button or not. 
		if ( rsvpList.size() > 0 ) {
			hasOneOrMoreRsvp = true;
		}
		
		
		String[] startTimeList = { "8:00am",	"8:30am",	"9:00am",	"9:30am",	"10:00am",	"10:30am",	"11:00am",	"11:30am",	"12:00pm",	"12:30pm",	"1:00pm",	"1:30pm",	"2:00pm",	"2:30pm",	"3:00pm",	"3:30pm",	"4:00pm",	"4:30pm",	"5:00pm",	"5:30pm",	"6:00pm",	"6:30pm",	"7:00pm",	"7:30pm",	"8:00pm",	"8:30pm"};
		
		model.addAttribute("startTimeList", startTimeList ); 
		
		model.addAttribute("playdate", playdateObj);
		model.addAttribute("rsvpCount", rsvpCount); 
		model.addAttribute("aggKidsCount", aggKidsCount); 
		model.addAttribute("rsvpExistsCreatedByAuthUser", rsvpExistsCreatedByAuthUser);
		model.addAttribute("rsvpObjForAuthUser", rsvpObjForAuthUser); 
		model.addAttribute("rsvpList", rsvpList);
		
		model.addAttribute("aggAdultsCount", aggAdultsCount); 
		
		model.addAttribute("openKidsSpots", openKidsSpots); 
		
		model.addAttribute("hasOneOrMoreRsvp", hasOneOrMoreRsvp);
		
		
		// get/deliver list of unioned rsvp records
		List<PlaydateUserUnionRsvpUser> playdateRsvpList = rsvpSrv.playdateRsvpList(playdateId);
		model.addAttribute("playdateRsvpList", playdateRsvpList);
		return "playdate/edit.jsp";
	}
	
	// process the edit(s)
	@PostMapping("/playdate/edit")
	public String PostTheEditPlaydate(
			@Valid 
			@ModelAttribute("playdate") PlaydateMdl playdateMdl 
			, BindingResult result
			, Model model
			, HttpSession session
			, RedirectAttributes redirectAttributes
			) {
		
		// log out the unauth / deliver the auth use data
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		model.addAttribute("authUser", userSrv.findById(authenticatedUserId));
		
		// below now setting up playdate object by using the getID on the modAtt thing. 
		PlaydateMdl playdateObj = playdateSrv.findById(playdateMdl.getId());
		
		
		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		UserMdl recordCreatorUserMdl = playdateObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj 
		
		if(!currentUserMdl.equals(recordCreatorUserMdl)) {
			System.out.println("recordCreatorUserMdl != currentUserMdl, so redirected to record"); 
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "This record can only be edited by its creator.  Any edits just attempted were discarded.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
		

		if (result.hasErrors()) { 
			
//			model.addAttribute("playdate", playdateObj); // this line seems to overide the bonded Mdl object (including error stuff)
			model.addAttribute("validationErrorMsg", "Uh-oh! Please fix the errors noted below and submit again.  (Or cancel.)"); //redirectAttributes doesn't work here b/c we are not redirecting, we are merely returning.  so use modAtt instead.
			return "playdate/edit.jsp";
		} else {
			playdateMdl.setUserMdl(playdateObj.getUserMdl()); // shove the existing user mdl from the db/obj into the obj about to be saved. 
			playdateSrv.update(playdateMdl);
			return "redirect:/playdate/" + playdateObj.getId();
		}
	}
	

	
	// delete playdate
    @DeleteMapping("/playdate/{id}")
    public String deletePlaydate(
    		@PathVariable("id") Long playdateId
    		, HttpSession session
    		, RedirectAttributes redirectAttributes
    		) {
		// If no userId is found in session, redirect to logout.  JRF: put this on basically all methods now, except the login/reg pages
		if(session.getAttribute("userId") == null) {return "redirect:/logout";}
		Long authenticatedUserId = (Long) session.getAttribute("userId");
		// model.addAttribute("authUser", userSrv.findById(AuthenticatedUserId));
		
		PlaydateMdl playdateObj = playdateSrv.findById(playdateId);
		
		UserMdl currentUserMdl = userSrv.findById(authenticatedUserId); //  gets the userModel object by calling the user service with the session user id
		UserMdl recordCreatorUserMdl = playdateObj.getUserMdl();   // gets the userMdl obj saved to the existing playdateObj		
		
		if(!currentUserMdl.equals(recordCreatorUserMdl)) {
			System.out.println("recordCreatorUserMdl != currentUserMdl, so redirected to record"); 
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "This record can only be deleted by its creator.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
		
		List<RsvpMdl> rsvpList = playdateObj.getRsvpList(); // instantiate the java list	
		
		if ( rsvpList.size() > 0 ) {
			System.out.println("Delete hack attempted on playdate record");
			redirectAttributes.addFlashAttribute("permissionErrorMsg", "This event has rsvp records, so it cannot be deleted.  If all user RSVPs get deleted, you can then delete this event.  Event no longer happening?  Then update the playdateStatus to be Cancelled.");
			return "redirect:/playdate/" + playdateObj.getId();
		}
			
		playdateSrv.delete(playdateObj);
        return "redirect:/playdate";
    }
	

// end of methods
}
