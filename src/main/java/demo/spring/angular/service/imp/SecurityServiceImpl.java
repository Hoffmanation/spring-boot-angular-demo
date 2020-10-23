package demo.spring.angular.service.imp;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import demo.spring.angular.service.SecurityService;


/**
 * A service class provides the spring security layer of the mediation
 * application
 * 
 * @author Hoffman
 *
 */
@Component
public class SecurityServiceImpl implements SecurityService {

	/*
	 * Spring dependency injection
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static final Logger logger = LogManager.getLogger(SecurityServiceImpl.class);

	/**
	 * This method is custom implementation of the spring security get admin portal
	 * user
	 */
	@Override
	public String findLoggedInUsername() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (userDetails instanceof UserDetails) {
			return ((UserDetails) userDetails).getUsername();
		}
		return null;
	}

	/**
	 * This method is custom implementation of the spring security login method
	 */
	@Override
	public boolean autologin(String username, String password) {
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
			authenticationManager.authenticate(usernamePasswordAuthenticationToken);
			if (usernamePasswordAuthenticationToken.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				logger.info("A new successful Login to Admin Portal, Credentials that was given : User Name: " + username + ", Password: " + password);
				return true;
			}
			logger.error("User details are incorrect ,Credentials that was given : User Name:" + username + ", Password: " + password);
			return false;
		} catch (BadCredentialsException bc) {
			logger.error("Access to Admin Portal was denied due to Bad Credentials, Credentials that was given : User Name: " + username + ", Password: " + password);
		} catch (UsernameNotFoundException unf) {
			logger.error("Access to Admin Portal was denied due to a non existed user, Credentials that was given : User Name: " + username + ", Password: " + password);
		} catch (Exception e) {
			logger.error("An error occurred while trying to login to the Admin Portal");
			e.printStackTrace();
		}
		return false;
	}
}
