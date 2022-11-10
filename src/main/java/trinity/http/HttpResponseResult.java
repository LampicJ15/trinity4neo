package trinity.http;

import java.util.List;
import java.util.Map;

public class HttpResponseResult {

  public final Map<String, List<String>> headers;
  public final String body;
  public final Number statusCode;
  public final String error;

  public HttpResponseResult(
      Map<String, List<String>> headers,
      String body,
      Integer statusCode,
      String error) {
    this.headers = headers;
    this.body = body;
    this.statusCode = statusCode;
    this.error = error;
  }
}
