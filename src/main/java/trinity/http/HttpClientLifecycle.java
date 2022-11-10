package trinity.http;

import java.net.http.HttpClient;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;
import org.neo4j.logging.Log;

public class HttpClientLifecycle extends LifecycleAdapter {

  private final Log log;
  private final GlobalProcedures globalProcedures;

  public HttpClientLifecycle(Log log,
                             GlobalProcedures globalProcedures) {
    this.log = log;
    this.globalProcedures = globalProcedures;
  }

  @Override
  public void init() {
    log.debug("[trinity4neo] Registration of Http Client component.");
    globalProcedures.registerComponent(HttpClient.class,
        (context) -> HttpClient.newHttpClient(),
        true);
  }
}
