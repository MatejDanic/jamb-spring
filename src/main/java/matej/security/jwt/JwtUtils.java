package matej.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import matej.security.services.UserDetailsImpl;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${matej.jwtSecret}")
	private String jwtSecret;

	@Value("${matej.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				// .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.setExpiration(null)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	protected String getUsernameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public String getUsernameFromHeader(String headerAuth) {
        if (headerAuth.startsWith("Bearer ")) {
			String token = headerAuth.substring(7, headerAuth.length());
			if (token != null && validateJwtToken(token)) {
				return getUsernameFromJwtToken(token);
			} 
		}
		return null;
    }

	protected boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException exc) {
			logger.error("Invalid JWT signature: {}", exc.getMessage());
		} catch (MalformedJwtException exc) {
			logger.error("Invalid JWT token: {}", exc.getMessage());
		} catch (ExpiredJwtException exc) {
			logger.error("JWT token is expired: {}", exc.getMessage());
		} catch (UnsupportedJwtException exc) {
			logger.error("JWT token is unsupported: {}", exc.getMessage());
		} catch (IllegalArgumentException exc) {
			logger.error("JWT claims string is empty: {}", exc.getMessage());
		}

		return false;
	}

	
}