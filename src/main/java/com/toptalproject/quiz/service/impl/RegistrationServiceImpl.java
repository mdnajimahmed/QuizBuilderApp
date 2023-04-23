package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.config.AppClients;
import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.service.RegistrationService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

@Service
class RegistrationServiceImpl implements RegistrationService {
  private final AppClients appClients;

  RegistrationServiceImpl(final AppClients appClients) {
    this.appClients = appClients;
  }

  @Override
  public String getRedirectUri(String clientId) {
    final AppClients.AppClient appClient =
        appClients.getClientConfigs().stream().filter(c -> c.getClientId().equals(clientId))
            .findAny().orElseThrow(
                () -> new BadRequestException(String.format("Invalid client id %s", clientId))
            );
    final String redirectUriEncoded =
        URLEncoder.encode(appClient.getRedirectUri(), StandardCharsets.UTF_8);
    return String.format(
        "https://toptalquizapp.auth.ap-southeast-2.amazoncognito.com/signup?client_id=%s&response_type=code&scope=email+openid+phone&redirect_uri=%s",
        clientId, redirectUriEncoded);
  }
}
