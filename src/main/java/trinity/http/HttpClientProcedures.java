package trinity.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class HttpClientProcedures {

  @Context
  public HttpClient httpClient;

  @Procedure(value = "tnt.http.post", mode = Mode.READ)
  @Description("""
      Procedure sends a POST request using the Java Http Client and retrieves the response.
      In the configuration map we can specify the following parameters:
      - failOnError <true>: if we wish to throw an exception if the request fails
      - httpVersion <2>: specify the Http protocol version, possible values are 1.1 and 2
      """)
  public Stream<HttpResponseResult> sendPost(@Name("uri") String uri,
                                             @Name(value = "headers", defaultValue = "{}")
                                                 Map<String, String> headers,
                                             @Name(value = "requestBody", defaultValue = "null")
                                                 String requestBody,
                                             @Name(value = "configuration", defaultValue = "{}")
                                                 Map<String, Object> configuration)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequestConfiguration httpConfig = new HttpRequestConfiguration(configuration);
    return Stream.of(
        sendRequest(HttpRequestMethod.POST,
            uri,
            headers,
            requestBody,
            httpConfig.getHttpProtocolVersion(),
            httpConfig.isFailOnError()));
  }


  @Procedure(value = "tnt.http.put", mode = Mode.READ)
  public Stream<HttpResponseResult> sendPut(@Name("uri") String uri,
                                            @Name(value = "headers", defaultValue = "{}")
                                                Map<String, String> headers,
                                            @Name(value = "requestBody", defaultValue = "null")
                                                String requestBody,
                                            @Name(value = "configuration", defaultValue = "{}")
                                                Map<String, Object> configuration)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequestConfiguration httpConfig = new HttpRequestConfiguration(configuration);
    return Stream.of(
        sendRequest(HttpRequestMethod.PUT,
            uri,
            headers,
            requestBody,
            httpConfig.getHttpProtocolVersion(),
            httpConfig.isFailOnError()));
  }

  @Procedure(value = "tnt.http.get", mode = Mode.READ)
  public Stream<HttpResponseResult> sendGet(@Name("uri") String uri,
                                            @Name(value = "headers",
                                                defaultValue = "{}")
                                                Map<String, String> headers,
                                            @Name(value = "configuration",
                                                defaultValue = "{}")
                                                Map<String, Object> configuration)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequestConfiguration httpConfig = new HttpRequestConfiguration(configuration);
    return Stream.of(
        sendRequest(HttpRequestMethod.GET,
            uri,
            headers,
            null,
            httpConfig.getHttpProtocolVersion(),
            httpConfig.isFailOnError()));
  }

  @Procedure(value = "tnt.http.delete", mode = Mode.READ)
  public Stream<HttpResponseResult> sendDelete(@Name("uri") String uri,
                                               @Name(value = "headers", defaultValue = "{}")
                                                   Map<String, String> headers,
                                               @Name(value = "configuration", defaultValue = "{}")
                                                   Map<String, Object> configuration)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequestConfiguration httpConfig = new HttpRequestConfiguration(configuration);
    return Stream.of(
        sendRequest(HttpRequestMethod.DELETE,
            uri,
            headers,
            null,
            httpConfig.getHttpProtocolVersion(),
            httpConfig.isFailOnError()));
  }

  public HttpResponseResult sendRequest(HttpRequestMethod httpRequestMethod,
                                        String uri,
                                        Map<String, String> headers,
                                        String requestBody,
                                        HttpClient.Version version,
                                        boolean failOnError)
      throws URISyntaxException, IOException, InterruptedException {

    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .uri(new URI(uri));

      headers.forEach(requestBuilder::header);

      HttpRequest.BodyPublisher requestBodyPublisher = Optional
          .ofNullable(requestBody).map(HttpRequest.BodyPublishers::ofString)
          .orElse(HttpRequest.BodyPublishers.noBody());

      requestBuilder.method(httpRequestMethod.name(), requestBodyPublisher);
      requestBuilder.version(version);
      HttpRequest request = requestBuilder.build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      return new HttpResponseResult(response.headers().map(),
          response.body(),
          response.statusCode(),
          null);

    } catch (URISyntaxException | IOException | InterruptedException e) {
      if (failOnError) {
        throw e;
      } else {
        return new HttpResponseResult(null, null, null, e.getMessage());
      }
    }
  }
}
