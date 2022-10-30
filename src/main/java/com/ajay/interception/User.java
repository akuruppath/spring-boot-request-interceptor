package com.ajay.interception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class User {

  private String userName;
  private List<GrantedAuthority> authorities;

  public User(Pair<String, Claims> userDetails) {
    this.userName = userDetails.getKey();
    List<String> roles = (List<String>) userDetails.getValue().get("Role");
    authorities = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toCollection(ArrayList::new));
  }

}
