package com.landingapp.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.landingapp.security.filter.TokenValidationFilter;
import com.landingapp.security.service.NoOpRedirectStrategy;

import com.landingapp.security.service.TokenProvider;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
			new AntPathRequestMatcher("/user/**"),
			new AntPathRequestMatcher("/actuator/**"));

    private static final RequestMatcher PROTECTED_URLS = new  NegatedRequestMatcher(PUBLIC_URLS);


    private final TokenProvider tokenprovider;

    @Autowired
	public SecurityConfig(TokenProvider tokenprovider) {
		this.tokenprovider = tokenprovider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(tokenprovider);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(PUBLIC_URLS);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    .exceptionHandling()
		    .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
		    .and()
		    .authenticationProvider(tokenprovider)
		    .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
		    .authorizeRequests()
		    .requestMatchers(PROTECTED_URLS)
		    .authenticated()
		    .and()
		    .csrf().disable()
		    .formLogin().disable()
		    .httpBasic().disable()
		    .logout().disable();
		    
		    
	}
	
	@Bean
	TokenValidationFilter restAuthenticationFilter() throws Exception{
		
		final TokenValidationFilter filter = new TokenValidationFilter(PROTECTED_URLS);
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(successHandler());
		return filter ;
		
	}
	@Bean
	SimpleUrlAuthenticationSuccessHandler successHandler() {
		final SimpleUrlAuthenticationSuccessHandler successHandler =
				new SimpleUrlAuthenticationSuccessHandler();
		successHandler.setRedirectStrategy(new NoOpRedirectStrategy());
		return successHandler;
	}
	
    @Bean 
    public AuthenticationEntryPoint forbiddenEntryPoint() {
    	return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
    }
    
}
