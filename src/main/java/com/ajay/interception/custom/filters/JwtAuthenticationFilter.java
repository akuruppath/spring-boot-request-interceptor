package com.ajay.interception.custom.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ajay.interception.TokenBasedAuthentication;
import com.ajay.interception.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER = "Bearer ";

  @Value("${requestinterceptor.jwt.signingkey}")
  private String signingKey;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    Optional<String> optionalToken = getToken(request);
    if (optionalToken.isPresent()) {
      String bearerString = optionalToken.get();
      String token = bearerString.substring(BEARER.length());
      Optional<Claims> claims = getAllClaimsFromToken(token);
      Optional<String> username = getUsernameFromToken(claims);

      if (claims.isPresent() && username.isPresent()) {
        User user = new User(Pair.of(username.get(), claims.get()));
        TokenBasedAuthentication tokenBasedAuthentication = new TokenBasedAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(tokenBasedAuthentication);
      }
    }
    filterChain.doFilter(request, response);

  }

  private Optional<String> getUsernameFromToken(Optional<Claims> optionalClaims) {
    if (optionalClaims.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(optionalClaims.get().getSubject());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Optional<String> getToken(HttpServletRequest request) {
    String authrzHeader = request.getHeader("Authorization");
    return authrzHeader != null && authrzHeader.startsWith(BEARER) ? Optional.of(authrzHeader)
        : Optional.empty();
  }


  private Optional<Claims> getAllClaimsFromToken(String token) {

    try {
      return Optional
          .of(Jwts.parserBuilder().setSigningKey(signingKey.getBytes(StandardCharsets.UTF_8))
              .build().parseClaimsJws(token).getBody());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

}
