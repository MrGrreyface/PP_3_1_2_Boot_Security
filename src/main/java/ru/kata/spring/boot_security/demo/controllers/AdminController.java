package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping
    public String showAllUsers(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        List<User> userList = userService.getAll();
        model.addAttribute("userList", userList);
        return "users";
    }

    @GetMapping("/newUser")
    public String createNewUser(Model model) {
        User user = new User();
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "newUser-form";
    }

    @PostMapping("/save")
    public String saveNewUser (@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @RequestParam("roles") List<Role> roles) {
        if (bindingResult.hasErrors()) {
            return "newUser-form";
        }
        user.setRoles(roles.stream().collect(Collectors.toSet()));
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/update")
    public String updateUser(Model model, @PathVariable("id") Long id) {
        User user = userService.findById(id);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "edit-form";
    }

    @PostMapping("/{id}/update")
    public String saveUpdatedUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @RequestParam("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "edit-form";
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}
