package com.toptalproject.quiz;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;


@Service
public class TokenService {

  public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String username) {
    final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    final String message = username + userPoolClientId;
    SecretKeySpec signingKey =
        new SecretKeySpec(userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
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
  public String getToken(String userName,String password) throws NoSuchAlgorithmException {
    try {
      Map<String,String> authParameters = new HashMap<>();
      authParameters.put("USERNAME", userName);
      authParameters.put("PASSWORD", password);
      authParameters.put("SECRET_HASH", calculateSecretHash("3e074qfjrs0ba81g4p8tthhk5u",
          "8k6gp9bvmvnuhd2fthgh0rra86bbiejigkje3ulvsgisq5n6gut",userName));
      CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
          .region(Region.AP_SOUTHEAST_2)
          .credentialsProvider(ProfileCredentialsProvider.create())
          .build();

      AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
          .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
          .clientId("3e074qfjrs0ba81g4p8tthhk5u")
          .userPoolId("ap-southeast-2_5Xy5gOXtg")
          .authParameters(authParameters).build();

      AdminInitiateAuthResponse authResult = identityProviderClient.adminInitiateAuth(authRequest);
      return authResult.authenticationResult().idToken();
    } catch(CognitoIdentityProviderException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      throw new RuntimeException(e);
    }
  }
}
