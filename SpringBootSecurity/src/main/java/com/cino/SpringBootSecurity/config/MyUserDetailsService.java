package com.cino.SpringBootSecurity.config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cino.SpringBootSecurity.model.Users;
import com.cino.SpringBootSecurity.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		// carica l'utente da database
		
		Optional<Users> userByUsername = usersRepository.findByUsername(username);
		if (!userByUsername.isPresent()) {
			log.error("Could not find user with that username: {}", username);
            throw new UsernameNotFoundException("Invalid credentials!");
		}
		
		// controlla che la user sia valida
		
		Users user = userByUsername.get();
        if (user == null || !user.getUsername().equals(username)) {
        	log.error("Could not find user with that username: {}", username);
            throw new UsernameNotFoundException("Invalid credentials!");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        user.getAuthorities().stream().forEach(authority -> 
        grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority())));
        
        // converte l'tuente in un UserDetails
        
        return User.builder()
        		.username(user.getUsername())
        		.password(user.getPassword())
        		.authorities(grantedAuthorities)
        		.disabled(!user.getEnabled())
        		.build();
	}
}
