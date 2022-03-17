package com.karitoreyes.project.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.karitoreyes.project.models.LoginUser;
import com.karitoreyes.project.models.User;
import com.karitoreyes.project.repositories.UserRepository;
    
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;
    
    public User register(User newUser, BindingResult result) {
    	Optional<User> potentialUser = userRepo.findByEmail(newUser.getEmail());
    	if(!potentialUser.isPresent()) {
    		String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(10));
    		newUser.setPassword(hashed);
    		User user = userRepo.save(newUser);
    		return user;
    	}else {
    		return null;
    	}
    }
    public User login(LoginUser newLoginObject, BindingResult result) {
    	Optional<User> potentialUser = userRepo.findByEmail(newLoginObject.getEmail());
    	if(potentialUser.isPresent()) {
    		User user = potentialUser.get();
    		return user;
    	}else {
    		return null;
    	}
    }
}