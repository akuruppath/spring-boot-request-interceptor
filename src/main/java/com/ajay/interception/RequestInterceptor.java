package com.ajay.interception;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.ajay.interception.custom.annotations.HasAllRoles;
import com.ajay.interception.custom.annotations.HasAnyRole;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws ServletException {
    log.info("Inside prehandle");
    Collection<? extends GrantedAuthority> availableAuthorities = extractAuthorities();
    HandlerMethod hm;
    try {
      hm = (HandlerMethod) handler;
    } catch (ClassCastException e) {
      throw e;
    }
    Method method = hm.getMethod();


    if (method.isAnnotationPresent(HasAllRoles.class)
        && method.isAnnotationPresent(HasAnyRole.class)) {
      throw new IllegalStateException("Can specify only one of the annotations on a method.");
    }

    if (method.isAnnotationPresent(HasAllRoles.class)) {
      return areAllRolesPresent(availableAuthorities, method);
    }

    if (method.isAnnotationPresent(HasAnyRole.class)) {
      return isAnyRolePresent(availableAuthorities, method);
    }

    return false;
  }


  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      @Nullable ModelAndView modelAndView) throws Exception {
    log.info("Inside posthandle");
  }


  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, @Nullable Exception ex) throws Exception {
    log.info("Inside after completion");
  }


  private boolean areAllRolesPresent(Collection<? extends GrantedAuthority> availableAuthorities,
      Method method) {
    String[] roles = method.getAnnotation(HasAllRoles.class).roles();
    ArrayList<SimpleGrantedAuthority> specifiedAuthorities =
        Arrays.stream(roles).map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toCollection(ArrayList::new));
    return availableAuthorities.containsAll(specifiedAuthorities);
  }

  private boolean isAnyRolePresent(Collection<? extends GrantedAuthority> availableAuthorities,
      Method method) {
    String[] roles = method.getAnnotation(HasAnyRole.class).roles();
    ArrayList<SimpleGrantedAuthority> specifiedAuthorities =
        Arrays.stream(roles).map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toCollection(ArrayList::new));
    return availableAuthorities.stream().anyMatch(specifiedAuthorities::contains);
  }

  private Collection<? extends GrantedAuthority> extractAuthorities() {
    TokenBasedAuthentication tokenBasedAuthentication =
        (TokenBasedAuthentication) SecurityContextHolder.getContext().getAuthentication();
    return tokenBasedAuthentication.getAuthorities() == null ? Collections.emptySet()
        : tokenBasedAuthentication.getAuthorities();
  }

}
