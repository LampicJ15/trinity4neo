package trinity.http;

import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.extension.ExtensionFactory;
import org.neo4j.kernel.extension.ExtensionType;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.logging.internal.LogService;

public class HttpClientExtensionFactory
    extends ExtensionFactory<HttpClientExtensionFactory.Dependencies> {

  public HttpClientExtensionFactory() {
    super(ExtensionType.GLOBAL, "HTTP CLIENT FOR NEO4J");
  }

  @Override
  public Lifecycle newInstance(ExtensionContext extensionContext, Dependencies dependencies) {
    return new HttpClientLifecycle(dependencies.log().getUserLog(
        HttpClientLifecycle.class),
        dependencies.globalProceduresRegistry());
  }

  public interface Dependencies {
    LogService log();

    GlobalProcedures globalProceduresRegistry();
  }
}
