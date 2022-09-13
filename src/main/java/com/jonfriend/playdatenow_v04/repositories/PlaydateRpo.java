package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jonfriend.playdatenow_v04.models.PlaydateMdl;

@Repository
public interface PlaydateRpo extends CrudRepository<PlaydateMdl, Long> {
	
	List<PlaydateMdl> findAll();
	
	PlaydateMdl findByIdIs(Long id);
	 
	 @Query(
			 value= "SELECT p.* FROM playdatenow.playdate p WHERE p.createdBy_id LIKE :keyword and event_date >= curdate() order by p.event_date desc"
			 , nativeQuery = true)
	 List<PlaydateMdl> userHostedPlaydateListCurrentPlus(Long keyword);
	 
	 @Query(
			 value= "SELECT p.* FROM playdatenow.playdate p WHERE p.createdBy_id LIKE :keyword and event_date < curdate() order by p.event_date desc"
			 , nativeQuery = true)
	 List<PlaydateMdl> userHostedPlaydateListPast(Long keyword);

// end rpo
}
