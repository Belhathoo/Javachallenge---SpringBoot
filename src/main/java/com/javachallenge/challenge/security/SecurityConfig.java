package com.javachallenge.challenge.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.javachallenge.challenge.security.jwt.JwtTokenVerifier;
import com.javachallenge.challenge.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final JwtTokenVerifier jwtTokenVerifier;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().anyRequest().permitAll();
		http.addFilterBefore(jwtTokenVerifier, UsernamePasswordAuthenticationFilter.class);

	}
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
}







	// @Override
	// 	http.csrf().disable();
	// protected void configure(HttpSecurity http) throws Exception {
	// 	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	// 	http.authorizeRequests().antMatchers("/api/users/generate", "/api/users/batch").permitAll();
	// 	// http.authorizeRequests().antMatchers("/swagger-ui/**",
	// 	// "/v3/api-docs/**").permitAll();
	// 	// http.authorizeRequests().antMatchers(HttpMethod.POST,
	// 	// "/api/auth").permitAll();
	// 	// http.authorizeRequests().anyRequest().authenticated();
	// 	// http.addFilterBefore(tokenVerifierFilter,
	// 	// UsernamePasswordAuthenticationFilter.class);
	// }

	// @Override
	// protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	// 	auth.authenticationProvider(daoAuthenticationProvider());
	// }

	// @Bean
	// public DaoAuthenticationProvider daoAuthenticationProvider() {
	// 	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	// 	provider.setPasswordEncoder(bCryptPasswordEncoder);
	// 	provider.setUserDetailsService(userService);
	// 	return provider;
	// }