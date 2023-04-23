package com.toptalproject.quiz;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;


@Service
public class CognitoTokenService {

  @Value("${POOL_ID}")
  private String poolId;
  @Value("${WEB_CLIENT_ID}")
  private String clientId;
  @Value("${WEB_CLIENT_SECRET}")
  private String clientSecret;

  public String calculateSecretHash(String username) {
    final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    final String message = username + clientId;
    SecretKeySpec signingKey =
        new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8),
            HMAC_SHA256_ALGORITHM);
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);
      byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException("Error while calculating ");
    }
  }

  public String getToken(String userName, String password) throws NoSuchAlgorithmException {
    try {
      Map<String, String> authParameters = new HashMap<>();
      authParameters.put("USERNAME", userName);
      authParameters.put("PASSWORD", password);
      authParameters.put("SECRET_HASH", calculateSecretHash(userName));
      CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
          .region(Region.AP_SOUTHEAST_2)
          .credentialsProvider(ProfileCredentialsProvider.create())
          .build();

      InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
          .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
          .clientId(clientId)
          .authParameters(authParameters).build();
      InitiateAuthResponse authResult = identityProviderClient.initiateAuth(authRequest);
      return authResult.authenticationResult().idToken();
    } catch (CognitoIdentityProviderException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      throw new RuntimeException(e);
    }
  }
}
