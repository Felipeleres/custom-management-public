package com.felipeleres.customermanagement.services;

import com.felipeleres.customermanagement.entities.Role;
import com.felipeleres.customermanagement.entities.User;
import com.felipeleres.customermanagement.projections.UserDetailsProjection;
import com.felipeleres.customermanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
        if(result.size() == 0 ){
            throw new UsernameNotFoundException("User not Found!");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());

        for(UserDetailsProjection projection : result ){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;

    }
}
