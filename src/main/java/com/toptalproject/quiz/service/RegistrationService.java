package com.toptalproject.quiz.service;

import java.util.Optional;

public interface RegistrationService {
  String getRedirectUri(Optional<String> clientId);
}
