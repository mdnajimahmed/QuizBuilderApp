package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.service.RegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
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
      final HttpServletResponse response,
      @RequestParam(value = "clientId", required = false) final String clientId)
      throws IOException {
    response.sendRedirect(signUpService.getRedirectUri(Optional.ofNullable(clientId)));
  }
}
