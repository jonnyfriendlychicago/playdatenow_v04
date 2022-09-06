package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

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

}
