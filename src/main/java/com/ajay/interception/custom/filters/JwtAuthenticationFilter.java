package com.ajay.interception.custom.filters;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ajay.interception.TokenBasedAuthentication;
import com.ajay.interception.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    Optional<String> optionalToken = getToken(request);
    if (optionalToken.isPresent()) {
      String bearerString = optionalToken.get();
      String token = bearerString.substring("Bearer ".length());
      Claims claims = getAllClaimsFromToken(token);
      String username = getUsernameFromToken(claims);

      User user = new User(Pair.of(username, claims));

      TokenBasedAuthentication tokenBasedAuthentication = new TokenBasedAuthentication(user);
      SecurityContextHolder.getContext().setAuthentication(tokenBasedAuthentication);
    }
    filterChain.doFilter(request, response);

  }

  private Optional<String> getToken(HttpServletRequest request) {
    String authrzHeader = request.getHeader("Authorization");
    return authrzHeader != null && authrzHeader.startsWith("Bearer ") ? Optional.of(authrzHeader)
        : Optional.empty();
  }

  public String getUsernameFromToken(Claims claims) {
    String username;
    try {
      username = claims.getSubject();
    } catch (Exception e) {
      username = null;
    }
    return username;
  }

  private Claims getAllClaimsFromToken(String token) {
    Claims claims;
    try {
      claims = Jwts.parserBuilder().setSigningKey(
          "hZx7mGq6SInR6Crsphmy9GgTnCGJclSI4Qe4FKrUVhrrGcGQ5Yxjlgv55mUH54d3FOS3L53aoSgydsVwRqmuhvajcjSHFrFXqPNW9gGweUVaEGTM7Q3OgFf7e1cE8a2Y"
              .getBytes(Charset.forName("UTF-8")))
          .build().parseClaimsJws(token).getBody();
    } catch (Exception e) {
      claims = null;
    }
    return claims;
  }

}
