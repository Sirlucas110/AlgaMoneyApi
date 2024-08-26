/*package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Profile("basic-security")
@EnableWebSecurity
@Configuration
public class BasicSecurityConfig {
    
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	protected void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
 	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 		return http
 			.authorizeHttpRequests((
 				authorizeHttpRequests) ->
 					authorizeHttpRequests
 						.anyRequest().authenticated())
 			.httpBasic(Customizer.withDefaults())
 			.csrf(csrf -> csrf.disable())
 			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 			.build();
 		    
 	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}*/