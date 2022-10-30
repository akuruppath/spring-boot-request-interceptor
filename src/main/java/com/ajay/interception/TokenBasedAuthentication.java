package com.ajay.interception;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenBasedAuthentication extends AbstractAuthenticationToken {

  private static final long serialVersionUID = -321174991510946667L;
  private String token;
  private User user;

  public TokenBasedAuthentication(Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
  }

  public TokenBasedAuthentication(User user) {
    super(user.getAuthorities());
    this.user = user;
  }


  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }

}
