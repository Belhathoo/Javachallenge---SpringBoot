package com.javachallenge.challenge.security;

import com.javachallenge.challenge.security.jwt.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenVerifier jwtTokenVerifier;
	private final SecurityExceptionHandlerConfig exceptionHandlerConfig;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.exceptionHandling(exceptionHandling -> exceptionHandling
					.authenticationEntryPoint(exceptionHandlerConfig.authenticationEntryPoint())
					.accessDeniedHandler(exceptionHandlerConfig.accessDeniedHandler())
				)
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers(new AntPathRequestMatcher("/api/users/generate")).permitAll()
						.requestMatchers(new AntPathRequestMatcher("/api/users/batch")).permitAll()
						.requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
						.requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
						.requestMatchers(new AntPathRequestMatcher("/console/**")).permitAll()
						.requestMatchers(new AntPathRequestMatcher("/api/auth")).permitAll()
						.anyRequest().authenticated());
		httpSecurity.addFilterBefore(jwtTokenVerifier, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}
}
