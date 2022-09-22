package com.jonfriend.playdatenow_v04.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jonfriend.playdatenow_v04.models.RoleMdl;

@Repository
public interface RoleRpo extends CrudRepository<RoleMdl, Long> {

	List<RoleMdl> findAll();
    
    List<RoleMdl> findByName(String name);
}