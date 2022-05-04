package com.landingapp.security.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

public class NoOpRedirectStrategy  implements RedirectStrategy{

	@Override
	public void sendRedirect(HttpServletRequest request, 
			HttpServletResponse response, String url) throws IOException {
		
		
		
	}

}
