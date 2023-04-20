package com.toptalproject.quiz.controller;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NameController {
  @GetMapping("/test/name")
  public String getName(Principal principal){
    return "Najim";
  }
  @GetMapping("/welcome/email")
  public String getEmail(Principal principal){
    return "najim.ju@gmail.com";
  }
}
