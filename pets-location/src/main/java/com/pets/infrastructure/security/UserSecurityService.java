package com.pets.infrastructure.security;

import com.pets.infrastructure.persistence.SpringDataUserRepository;
import com.pets.infrastructure.persistence.entities.UserEntity;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSecurityService implements UserDetailsService {

    private final SpringDataUserRepository springDataUserRepository;

    @Autowired
    public UserSecurityService(SpringDataUserRepository springDataUserRepository){
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = springDataUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found."));

        String[] roles = userEntity.getRoles().stream()
                .map(UserRoleEntity::getRole)
                .toArray(String[]::new);

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(grantedAuthorities(roles))
                .build();
    }
    private String[] getAuthorities(String role){
        if("ADMIN".equals(role) || "CUSTOMER".equals(role)){
            return new String[] {"random_order"};
        }
        return new String[] {};
    }

    private List<GrantedAuthority> grantedAuthorities(String[] roles){
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
        for(String role: roles){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            for (String authority: this.getAuthorities(role)){
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return authorities;
    }
}
