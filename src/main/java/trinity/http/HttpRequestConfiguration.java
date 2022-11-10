package trinity.http;

import java.net.http.HttpClient;
import java.util.Map;

public class HttpRequestConfiguration {

  private final boolean failOnError;
  private final HttpClient.Version httpProtocolVersion;

  public HttpRequestConfiguration(Map<String, Object> configuration) {
    this.failOnError = (boolean) configuration.getOrDefault("failOnError", true);
    this.httpProtocolVersion = getHttpProtocolVersion(configuration);
  }

  private HttpClient.Version getHttpProtocolVersion(Map<String, Object> configuration) {
    String version = (String) configuration.getOrDefault("httpVersion", "1.1");
    return switch (version) {
      case "1.1" -> HttpClient.Version.HTTP_1_1;
      case "2" -> HttpClient.Version.HTTP_2;
      default -> throw new IllegalArgumentException(
          "Available HTTP versions are 1.1 and 2, but the provided was: " + version);
    };
  }

  public boolean isFailOnError() {
    return failOnError;
  }

  public HttpClient.Version getHttpProtocolVersion() {
    return httpProtocolVersion;
  }

}
