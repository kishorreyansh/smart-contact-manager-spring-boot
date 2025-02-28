package com.smartcontact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Method for adding common data for response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("User Name is :" + userName);
        User user = userRepository.getUserByUserName(userName);
        System.out.println("User Details: " + user);
        model.addAttribute("user", user);
    }

    // Dashboard Home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // Open Add Contact Handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
            Principal principal, HttpSession session) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if (file.isEmpty()) {
                System.out.println("File Cannot be Empty");
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("/static/images").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is Uploaded successfully !!!");
            }

            user.getContacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);
            System.out.println("Contact:     " + contact);
            session.setAttribute("message", new Message("Your Contact Added !!", "success"));
        } catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong !!", "danger"));
        }

        return "normal/add_contact_form";
    }

}
