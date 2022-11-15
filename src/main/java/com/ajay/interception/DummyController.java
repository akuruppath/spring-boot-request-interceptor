package com.ajay.interception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ajay.interception.custom.annotations.NeedAllRoles;
import com.ajay.interception.custom.annotations.NeedAnyRole;

@RestController
public class DummyController {

  @GetMapping(value = "/checkAll")
  @NeedAllRoles(roles = {"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
  public String checkAll() {
    return "200 ok";
  }

  @GetMapping(value = "/checkAny")
  @NeedAnyRole(roles = {"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
  public String checkAny() {
    return "200 ok";
  }

}
