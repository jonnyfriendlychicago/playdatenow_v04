package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jonfriend.playdatenow_v04.models.PlaydateMdl;

@Repository
public interface PlaydateRpo extends CrudRepository<PlaydateMdl, Long> {
	
	List<PlaydateMdl> findAll();
	
//	// approach 1
//	// below attempt to use advanced query
//	@Query("SELECT p.* FROM playdate p")
//	List<PlaydateMdl> findAllQuery(); 
//	
//	// approach 2
//	@Query("select e from Entity e "
//	          + "where (:field1='' or e.field1=:field1) "
//	          + "and (:field2='' or e.field2=:field2) "
//	          // ...
//	          + "and (:fieldN='' or e.fieldN=:fieldN)"
//	 Page<Entity> advancedSearch(@Param("field1") String field1,
//	                               @Param("field2") String field2,
//	                               @Param("fieldN") String fieldN,
//	                               Pageable page);
//	
//// approach 3
	          
	          
	PlaydateMdl findByIdIs(Long id);
	
	
}
