package com.jonfriend.playdatenow_v04.pojos;

public interface PlaydateUserUnionRsvpUser {

	 String getUserName(); 
	
	 Integer getKidCount() ; 
	
	  Integer getAdultCount() ; 
	
	  String getComment(); 
	
	  Integer getUserId() ; 
	
	  String getRsvpStatus() ; 
	  
	  String getFirstName(); 

	//	 "select u.user_name as userName, r.kid_count as kidCount, r.adult_count as adultCount, r.rsvp_status as rsvpStatus, r.comment as comment, u.id as userId from playdatenow.rsvp r left join playdatenow.user u on r.createdby_id = u.id  where r.playdate_id = :keyword"
}
