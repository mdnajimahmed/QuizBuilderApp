package com.toptalproject.quiz.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppClients {
  private List<AppClient> clientConfigs;
  @Data
  public static class AppClient {
    private String clientId;
    private String redirectUri;
  }
}
