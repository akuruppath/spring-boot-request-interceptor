package com.ajay.interception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

  @GetMapping(value = "/dummy")
  @NeedAllRoles(roles = {"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
  public String getDummy() {
    return "200 ok";
  }

}
