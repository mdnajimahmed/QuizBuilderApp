package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.service.RegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
  private final RegistrationService signUpService;
  public RegistrationController(final RegistrationService signUpService) {
    this.signUpService = signUpService;
  }

  @GetMapping("/sign-up")
  public void signUp(
      final HttpServletResponse response, @RequestParam("clientId") final String clientId)
      throws IOException {
    response.sendRedirect(signUpService.getRedirectUri(clientId));
  }
}
