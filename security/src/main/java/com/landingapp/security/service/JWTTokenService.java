package com.landingapp.security.service;
import org.apache.commons.lang3.StringUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
@Component
public class JWTTokenService implements TokenService,Clock {

	private static final String DOT=".";
	private static final GzipCompressionCodec COMPRETION_CODEC=new GzipCompressionCodec();
	String issuer;
	int experationInSec;
	int clockSkewSec;
	String secretkey;
	
	@Autowired
	public JWTTokenService(@Value("${jwt.issuer}") String issuer, 
			               @Value("${jwt.expiration-sec}") int experationInSec, 
			               @Value("${jwt.clock-skew-sec}") int clockSkewSec, 
			               @Value("${jwt.secret}") String secretkey) {
		
		this.issuer = issuer;
		this.experationInSec = experationInSec;
		this.clockSkewSec = clockSkewSec;
		this.secretkey = secretkey;
	}

	@Override
	public Date now() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String permanent(Map<String, String> attributes) {
		
		return newToken(attributes, 0  );
	}

	@Override
	public String expiring(Map<String, String> attributes) {
		
		return newToken(attributes, experationInSec);
	}

	@Override
	public Map<String, String> untrusted(String token) {
		final String noSignature = StringUtils.substringBeforeLast(token,DOT)+DOT;
		return parseClaims(()-> getparser().parseClaimsJwt(token).getBody());
	}

	

	@Override
	public Map<String, String> verify(String token) {
		final JwtParser parser = getparser()
				.setSigningKey(secretkey);
		
		return parseClaims(()-> parser.parseClaimsJwt(token).getBody());
	}
 
	private String newToken(final Map<String, String> attributes, final int expiresInSec) {
		final LocalDateTime currentTime= LocalDateTime.now();
		final Claims claims= Jwts.claims()
				                 .setIssuer(issuer)
				                 .setIssuedAt(Date.from(currentTime.toInstant(ZoneOffset.UTC)));
		if(expiresInSec > 0) {
			final LocalDateTime expireAt = currentTime.plusSeconds(expiresInSec);
			claims.setExpiration(Date.from(expireAt.toInstant(ZoneOffset.UTC)));
		}
		claims.putAll(attributes);
		
		return Jwts.builder()
				   .setClaims(claims)
				   .signWith(SignatureAlgorithm.HS256, secretkey)
				   .compressWith(COMPRETION_CODEC)
				   .compact();
	}
	private static Map<String,String> parseClaims(final Supplier<Claims> toClaims){
		try {
			final Claims claims= toClaims.get();
			final Builder<String,String> builder=ImmutableMap.builder();
			claims.entrySet().stream().forEach(e -> builder.put(e.getKey(),String.valueOf(e.getValue())));
			return builder.build();
		}catch(final IllegalArgumentException | JwtException e) {
			return ImmutableMap.of();
		}
		
		}
	private JwtParser getparser() {
		return Jwts.parser()
				.requireIssuer(issuer)
				.setClock(this)
				.setAllowedClockSkewSeconds(clockSkewSec);
	}
}
