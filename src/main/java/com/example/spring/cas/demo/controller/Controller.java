package com.example.spring.cas.demo.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping("/secure/")
    public String login(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username",userDetails.getUsername());
        model.addAttribute("roles",userDetails.getAuthorities());
        return "loggedInSuccessFully";

    }

    @GetMapping("/free")
    public String login1(HttpServletRequest request) {

        return "index";
    }


    @GetMapping("/logoutSuccess")
    public String logoutsuccess(HttpServletRequest request) {

        return "logoutSuccess";
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public ModelAndView defaultView(HttpServletRequest request, HttpServletResponse response) {
        String pageName = "index.html";
        ModelAndView view = new ModelAndView(pageName);
        return view;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String loggedIn(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user, Model model) {
        model.addAttribute("auths",mtoString(user.getAuthorities()));
        return "loggedInSuccessFully";
    }

    private String mtoString(Collection<GrantedAuthority> authorities) {
        StringBuilder res =new StringBuilder();
        authorities.stream().parallel().forEach(grantedAuthority -> {
            res.append(grantedAuthority.getAuthority());
        });

        return res.toString();
    }


}
