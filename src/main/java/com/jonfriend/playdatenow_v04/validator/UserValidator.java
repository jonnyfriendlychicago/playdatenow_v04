package com.jonfriend.playdatenow_v04.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.jonfriend.playdatenow_v04.models.UserMdl;
import com.jonfriend.playdatenow_v04.repositories.UserRpo;

@Component
public class UserValidator implements Validator {
    

	@Autowired
	private UserRpo userRpo;
	
	@Override
    public boolean supports(Class<?> clazz) {
        return UserMdl.class.equals(clazz);
    }
    
    @Override
    public void validate(
    		Object object
    		, Errors errors
    		) {
    	
    	UserMdl userMdl = (UserMdl) object;
        
        if (!userMdl.getPasswordConfirm().equals(userMdl.getPassword())) {
            errors.rejectValue("passwordConfirm", "Match");
        }     
        
        System.out.println("userMdl.getEmail(): " + userMdl.getEmail()); 
        
        Optional<UserMdl> userObjWithSameEmail = Optional.ofNullable(userRpo.findByEmail(userMdl.getEmail()));
        
        Optional<UserMdl> userObjWithSameUserName = Optional.ofNullable(userRpo.findByUserName(userMdl.getUserName()));
        
     // Reject if email exists in db
    	if(userObjWithSameEmail.isPresent()) {
    		errors.rejectValue("email", "Match");
    	}
    	
        // Reject if email exists in db
       	if(userObjWithSameUserName.isPresent()) {
       		errors.rejectValue("userName", "Match");
       	}
    }

// end validator class
}

