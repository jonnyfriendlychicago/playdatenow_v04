package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jonfriend.playdatenow_v04.models.UserMdl;

@Repository
public interface UserRpo extends CrudRepository<UserMdl, Long> {
    
//    Optional<UserMdl> findByEmail(String email);
	// above replaced by below, for SpringSec
	UserMdl findByEmail(String email);
   
//    Optional<UserMdl> findByUserName(String userName);     // adding to enforce username, too
    // above replaced by below, for SpringSec
    UserMdl findByUserName(String userName);
    
    List<UserMdl> findAll();
    
    UserMdl findByIdIs(Long id);
    
// end rpo   
}