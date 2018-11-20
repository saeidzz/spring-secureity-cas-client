package com.example.spring.cas.demo.service.impl;
import com.surena.security.exception.service.UserNotFoundException;
import com.surena.security.model.User;
import com.surena.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomUserDetailService implements com.example.spring.cas.demo.service.CustomeUserDetailService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    User user=null;

        try {
            user = userService.getUserByUsername(s);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(),
                user.getPassword(),
                true,
                true,
                true,
                true, AuthorityUtils.createAuthorityList("ROLE_"+user.getRoleInstances().get(0).getRole().getName()));
    }

    @Override
    public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
        User user=null;

        try {
            user = userService.getUserByUsername(authentication.getName());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(),
                user.getPassword(),
                true,
                true,
                true,
                true, AuthorityUtils.createAuthorityList("ROLE_"+user.getRoleInstances().get(0).getRole().getName()));
    }

}
