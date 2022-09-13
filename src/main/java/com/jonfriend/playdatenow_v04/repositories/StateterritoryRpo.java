package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.jonfriend.playdatenow_v04.models.StateterritoryMdl;

@Repository
public interface StateterritoryRpo extends CrudRepository<StateterritoryMdl, Long> {
	
	List<StateterritoryMdl> findAll();

// end rpo
}
