package com.jonfriend.playdatenow_v04.services;

import com.jonfriend.playdatenow_v04.models.UserMdl;
import com.jonfriend.playdatenow_v04.repositories.RoleRpo;
import com.jonfriend.playdatenow_v04.repositories.UserRpo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

//import java.util.ArrayList;
//import org.mindrot.jbcrypt.BCrypt;

//import com.jonfriend.playdatenow_v04.models.LoginUserMdl;

@Service
public class UserSrv{
    
    @Autowired
    private UserRpo userRpo;
    
	@Autowired
	private RoleRpo roleRpo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//    // begin: legacy register service
//	public UserMdl register(
//    		UserMdl newUser
//    		, BindingResult result
//    		) {
//        
//    	Optional<UserMdl> potentialUserEmail = userRpo.findByEmail(newUser.getEmail());
//    	
//    	// Reject if email exists in db
//    	if(potentialUserEmail.isPresent()) {
//    		result.rejectValue("email", "Matches", "An account with that email already exists.");
//    	}
//    	
//    	// above boilerplated into below to enforce unique usernames
//    	
//    	Optional<UserMdl> potentialUserUserName = userRpo.findByUserName(newUser.getUserName());
//    	
//    	// Reject if email exists in db
//    	if(potentialUserUserName.isPresent()) {
//    		result.rejectValue("userName", "Matches", "An account with that username already exists.");
//    	}
//    	
//    	// end new username stuff
//    	
//    	// reject if pw/confirm don't match
//    	if(!newUser.getPassword().equals(newUser.getConfirm())) {
//    		result.rejectValue("confirm", "Matches", "Password and Confirm Pasword must be the same.");
//    	}
//    	
//        // Return null if result has errors
//        if(result.hasErrors()) {
//            return null; // Exit the method and go back to the controller to handle the response
//        }
//        
//        // Hash and set password, save user to database
//        String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
//        newUser.setPassword(hashed);
//    	return userRpo.save(newUser);
//    }
//    // begin: legacy register service
    
    public void newUser(
    		UserMdl usermdl
    		, String role  // note: this is just a string variable called 'role'... not the roleMdl
    		) {
    	usermdl.setPassword(bCryptPasswordEncoder.encode(usermdl.getPassword()));
    	usermdl.setRoleMdl(roleRpo.findByName(role));
    	userRpo.save(usermdl);
	}
    
    // JRF: temporarily disabling this updateUser program.
//    public void updateUser(
//    		UserMdl usermdl
//    		) {
//    	userRpo.save(usermdl);
//	}
    
    public UserMdl findByEmail(
    		String email
    		) {
		return userRpo.findByEmail(email);
	}
	
	public List<UserMdl> allUsers(){
		return userRpo.findAll();
	}
	
//	// begin: legacy login
//    public UserMdl login(
//    		LoginUserMdl newLogin
//    		, BindingResult result
//    		) {
//    	
//    	Optional<UserMdl> potentialUser = userRpo.findByEmail(newLogin.getEmail());
//
//    	// Find user in the DB by email and reject if NOT present
//    	if(!potentialUser.isPresent()) {
//    		result.rejectValue("email", "Matches", "User email not found");
//    		return null;
//    	}
//    	
//    	// User exists, retrieve user from DB
//    	UserMdl user = potentialUser.get();
//    	
//    	if(!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
//    		result.rejectValue("password", "Matches", "Invalid Password.");
//    	}
//    	
//    	// Return null if result has errors
//    	if(result.hasErrors()) {
//    		// Exit the method and go back to the controller 	
//    		return null;
//    	}
//    	
//    	potentialUser.isPresent(); //notsureif needed 
//
//    	return user;
//    }
//	// end: legacy login
    
	// below disabled from springSec for now
//	public UserMdl upgradeUser(
//			UserMdl usermdl
//			) {
//		usermdl.setRoleMdl(roleRpo.findByName("ROLE_ADMIN"));
//		return userRpo.save(usermdl);
//	}

	// below disabled from springSec for now
//	public void deleteUser(
//			UserMdl usermdl
//			) {
//		userRpo.delete(usermdl);
//	}
	
	// below used on every page beginning with /home.  Delivers user object of logged in user, so as to display various user attributes. 
	// note, this did not change with springSec introduction
    public UserMdl findById(
    		Long id
    		) {
    	Optional<UserMdl> potentialUser = userRpo.findById(id);
       	
    	if(!potentialUser.isPresent()) {
       		return null;}
    		
       	return potentialUser.get();
    	
    }
     
    // returns all user (for to display list of users)
 	public List<UserMdl> returnAll(){
 		return userRpo.findAll();
 	}
 	
// 	// begin: legacy  updateUserProfile
//    public UserMdl updateUserProfile(
//    		UserMdl sketchedUpdatedUserMdl
//    		, BindingResult result
//    		) {
//        
//    	// try to find a user record with the proposed email addy
//    	Optional<UserMdl> userRecordWithMatchingEmailAddy = userRpo.findByEmail(sketchedUpdatedUserMdl.getEmail());
//    	
//    	// Reject if record found in db with that email and the userID of such record is NOT the user we mging here.
//    	if(
//    			userRecordWithMatchingEmailAddy.isPresent() 
//    			&&
//    			userRecordWithMatchingEmailAddy.get().getId() != sketchedUpdatedUserMdl.getId() 
//    			
//    	) {
//    		result.rejectValue("email", "Matches", "Another account with that email already exists.");
//    	}
//    	
//    	// above boilerplated into below to enforce unique usernames
//    	
//    	Optional<UserMdl> userRecordWithMatchingUserName = userRpo.findByUserName(sketchedUpdatedUserMdl.getUserName());
//    	
//    	// Reject if record found in db with that email and the userID of such record is NOT the user we mging here.
//    	if(
//    			userRecordWithMatchingUserName.isPresent() 
//    			&&
//    			userRecordWithMatchingUserName.get().getId() != sketchedUpdatedUserMdl.getId() 
//    			
//    	) {
//    		result.rejectValue("userName", "Matches", "Another account with that username already exists.");
//    	}
//    	
//    	// end new username stuff
//    	
//        if(result.hasErrors()) {
//            return null; // Exit the method and go back to the controller to handle the response
//        }
//        
//    	return userRpo.save(sketchedUpdatedUserMdl);
//    }
// 	// end: legacy  updateUserProfile
 	
    public UserMdl updateUserProfile(
    		UserMdl sketchedUpdatedUserMdl
    		, Errors errors
    		) {
        
    	// try to find a user record with the proposed email addy
    	Optional<UserMdl> userRecordWithMatchingEmailAddy = Optional.ofNullable(userRpo.findByEmail(sketchedUpdatedUserMdl.getEmail()));
    	
    	// Reject if record found in db with that email and the userID of such record is NOT the user we mging here.
    	if(
    			userRecordWithMatchingEmailAddy.isPresent() 
    			&&
    			userRecordWithMatchingEmailAddy.get().getId() != sketchedUpdatedUserMdl.getId() 
    			
    	) {
    		errors.rejectValue("email", "Match");
    	}

    	Optional<UserMdl> userRecordWithMatchingUserName = Optional.ofNullable(userRpo.findByUserName(sketchedUpdatedUserMdl.getUserName()));
    	
    	// Reject if record found in db with that userName and the userID of such record is NOT the user we mging here.
    	if(
    			userRecordWithMatchingUserName.isPresent() 
    			&&
    			userRecordWithMatchingUserName.get().getId() != sketchedUpdatedUserMdl.getId() 
    			
    	) {
    		errors.rejectValue("userName", "Match");
    	}

        if(errors.hasErrors()) {
            return null; // Exit the method and go back to the controller to handle the response
        }
        
    	return userRpo.save(sketchedUpdatedUserMdl);
    }

// end srv
}
