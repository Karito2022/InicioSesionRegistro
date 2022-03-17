package com.karitoreyes.project.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.karitoreyes.project.models.LoginUser;
import com.karitoreyes.project.models.User;
import com.karitoreyes.project.services.UserService;

@Controller
public class HomeController {

 @Autowired
 private UserService userServ;
 
 @GetMapping("")
 public String index(Model model, HttpSession session) {
	 User userSession = (User) session.getAttribute("user");
	 if(userSession != null){
		 model.addAttribute("user", userSession);
	     return "home.jsp";
	 }
     model.addAttribute("newUser", new User());
     model.addAttribute("newLogin", new LoginUser());
     return "index.jsp";
 }
 
 /*@GetMapping("/home")
 public String home(Model model, HttpSession session) {
	 User userSession = (User) session.getAttribute("user");
	 if(userSession == null){
		 return "redirect:/";
	 }
	 model.addAttribute("user", userSession);
     return "home.jsp";
 }*/
 
 @PostMapping("/register")
 public String register(@Valid @ModelAttribute("newUser") User newUser, 
         BindingResult result, Model model, HttpSession session) {
     
     if(result.hasErrors()) {
    	 model.addAttribute("newLogin", new LoginUser());
         return "index.jsp";
     }
     
     if(!newUser.getPassword().equals(newUser.getConfirm())) {
    	 result.rejectValue("confirm", "Matches", "La contraseña de confirmación debe coincidir");
    	 model.addAttribute("newLogin", new LoginUser());
    	 return "index.jsp";
     }
     
     User user = userServ.register(newUser, result);
     if(user == null) {
    	 result.rejectValue("email", "Matches", "El email ya se encuentra registrado.");
    	 model.addAttribute("newLogin", new LoginUser());
    	 return "index.jsp";
     }
     session.setAttribute("user", user);
     return "redirect:/";
 }
 
 @PostMapping("/login")
 public String login(@Valid @ModelAttribute("newLogin") LoginUser newLogin, 
         BindingResult result, Model model, HttpSession session) {

     if(result.hasErrors()) {
    	 model.addAttribute("newUser", new User());
         return "index.jsp";
     }
	 
     User user = userServ.login(newLogin, result);
     if(user == null) {
    	 result.rejectValue("email", "Matches", "El nombre usuario es incorrecto.");
    	 model.addAttribute("newUser", new User());
    	 return "index.jsp";
     }
     
     if(!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
    	 result.rejectValue("password", "Matches", "El password es incorrecto.");
    	 model.addAttribute("newUser", new User());
    	 return "index.jsp";
     }
     session.setAttribute("user", user);
     return "redirect:/";
 }
 
 @GetMapping("/logout")
 public String logout(HttpSession session) {
	 session.removeAttribute("user");
	 return "redirect:/";
 }
}