package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jonfriend.playdatenow_v04.models.UserMdl;

@Repository
public interface UserRpo extends CrudRepository<UserMdl, Long> {
    
	UserMdl findByEmail(String email);
   
    UserMdl findByUserName(String userName);
    
    List<UserMdl> findAll();
    
    UserMdl findByIdIs(Long id);
    
// end rpo   
}