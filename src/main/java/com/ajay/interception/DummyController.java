package com.ajay.interception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ajay.interception.custom.annotations.HasAllRoles;
import com.ajay.interception.custom.annotations.HasAnyRole;

@RestController
public class DummyController {

  @GetMapping(value = "/checkAll")
  @HasAllRoles(roles = {"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
  public String checkAll() {
    return "200 ok";
  }

  @GetMapping(value = "/checkAny")
  @HasAnyRole(roles = {"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
  public String checkAny() {
    return "200 ok";
  }

}
