package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.config.AppClients;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
  private final AppClients appClients;

  public RegistrationController(AppClients appClients) {
    this.appClients = appClients;
  }

  @GetMapping("/sign-up")
  public void getHealthCheck(
      final HttpServletResponse response, @RequestParam("clientId")final String clientId) throws IOException {
    AppClients.AppClient appClient =
        appClients.getClientConfigs().stream().filter(c -> c.getClientId().equals(clientId))
            .findAny().orElseThrow(
                () -> new BadRequestException(String.format("Invalid client id %s", clientId))
            );
    final String redirectUriEncoded = URLEncoder.encode(appClient.getRedirectUri(), StandardCharsets.UTF_8);
    response.sendRedirect(String.format("https://toptalquizapp.auth.ap-southeast-2.amazoncognito.com/signup?client_id=%s&response_type=code&scope=email+openid+phone&redirect_uri=%s",
        clientId,redirectUriEncoded));
  }
}
