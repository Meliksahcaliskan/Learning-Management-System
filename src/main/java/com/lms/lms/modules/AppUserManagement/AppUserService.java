package com.lms.lms.modules.AppUserManagement;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> user = repository.findByUsername(username);

        if (user.isPresent()) {
            var userObj = user.get();

            // Convert the user's role to a SimpleGrantedAuthority for Spring Security
            Collection<? extends GrantedAuthority> authorities = mapRolesToAuthorities(userObj.getRoleId());

            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities(authorities)
                    .build();
        } else {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(AppUser.Role role) {
    return Stream.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toList());
    }
}
