package org.launchcode.javawebdevtechjobsauthentication.controllers;

import org.launchcode.javawebdevtechjobsauthentication.models.User;
import org.launchcode.javawebdevtechjobsauthentication.models.data.UserRepository;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.LoginFormDTO;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.RegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;

    private static final String userSessionKey = "user";

    public User getUserFromSession(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(userSessionKey);
        if (userId == null) {
            return null;
        }

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            return null;
        }

        return user.get();
    }

    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey, user.getId());
    }

    @GetMapping("/register")
    public String displayRegistrationForm(Model model){
        model.addAttribute("title", "Register");
        model.addAttribute("registerFormDTO", new RegisterFormDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute @Valid RegisterFormDTO registerFormDTO, Errors errors,
                                          Model model, HttpServletRequest request){
        if (errors.hasErrors()){
            model.addAttribute("title", "Register");
            return "register";
        }
        User existingUser = userRepository.findByUsername(registerFormDTO.getUsername());

        if (existingUser != null){
            errors.rejectValue("username", "username.alreadyexists","Username already exists.");
            model.addAttribute("title", "Register");
            return "register";
        }
        String pass = registerFormDTO.getPassword();
        String verifyPass = registerFormDTO.getVerifyPassword();
        if(!pass.equals(verifyPass)){
            errors.rejectValue("password", "password.doesntmatch","Passwords must match.");
            model.addAttribute("title", "Register");
            return "register";
        }

        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(request.getSession(), newUser);

        return "redirect:";
    }

    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute("title", "Log In");
        model.addAttribute("loginFormDTO", new LoginFormDTO());
        return "login";
    }

    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO, Errors errors,
                                          Model model, HttpServletRequest request){
        if (errors.hasErrors()){
            model.addAttribute("title", "Log In");
            return "login";
        }
        User existingUser = userRepository.findByUsername(loginFormDTO.getUsername());
        System.out.println(existingUser);
        if (existingUser == null){
            errors.rejectValue("username", "username.doesntexists","Username doesn't exists.");
            model.addAttribute("title", "Log In");
            return "login";
        }
        String pass = loginFormDTO.getPassword();
        System.out.println(pass);
        if(!existingUser.isMatchingPassword(pass)){
            System.out.println("here");
            errors.rejectValue("password", "password.isntcorrect","Password is incorrect.");
            model.addAttribute("title", "Log In");
            return "login";
        }

        setUserInSession(request.getSession(), existingUser);

        return "redirect:";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
