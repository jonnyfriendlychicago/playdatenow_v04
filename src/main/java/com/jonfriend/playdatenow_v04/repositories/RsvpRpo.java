package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//import com.jonfriend.playdatenow_v04.models.TwintwoMdl;
import com.jonfriend.playdatenow_v04.models.RsvpMdl;
import com.jonfriend.playdatenow_v04.models.PlaydateMdl;

@Repository
public interface RsvpRpo extends CrudRepository<RsvpMdl, Long> {
	
	List<RsvpMdl> findAll();
	
	RsvpMdl findByIdIs(Long id);
	
	List<RsvpMdl> findAllByPlaydateMdl(PlaydateMdl playdateMdl);
	
	@Query(
			 value= 
//			 "select u.user_name, p.kid_count, p.adult_count, p.rsvp_status, p.comment from playdatenow.playdate p left join playdatenow.user u on p.createdby_id = u.id where p.id = :keyword"
//			 "select r.* from playdatenow.rsvp r where r.playdate_id = :keyword"
//			 "select r.kid_count, r.adult_count, r.rsvp_status, r.comment from playdatenow.rsvp r where r.playdate_id = :keyword"
//			 "select r.kid_count as kid_count, r.adult_count as adult_count, r.rsvp_status as rsvp_status, r.comment as comment from playdatenow.rsvp r where r.playdate_id = :keyword union all select p.kid_count as kid_count, p.adult_count as adult_count, p.rsvp_status as rsvp_status, p.comment as comment from playdatenow.playdate p where p.id = :keyword"
			 "select u.user_name as userName, r.kid_count as kidCount, r.adult_count as adultCount, r.rsvp_status as rsvpStatus, r.comment as comment, u.id as userId from playdatenow.rsvp r left join playdatenow.user u on r.createdby_id = u.id  where r.playdate_id = :keyword"
			 + " union all "
			 + "select u.user_name as userName, p.kid_count as kidCount, p.adult_count as adultCount, p.rsvp_status as rsvpStatus, p.comment as comment, u.id as userId from playdatenow.playdate p left join playdatenow.user u on p.createdby_id = u.id  where p.id = :keyword"
			 , nativeQuery = true)
	List<Object[]> playdateRsvpList(Long keyword);

}
