package talkdesk.challenge.core.runtime;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class Runner {
  public static void main(String[] args) {
    try {
      new Runner().run(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private void run(String[] args) throws Exception {
    Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      Manifest manifest = new Manifest(resources.nextElement().openStream());
      String nodeClassName = manifest.getMainAttributes().getValue("Node-Class");
      Class<? extends Node> cls = (Class<? extends Node>) Class.forName(nodeClassName);
      Node node = cls.getConstructor().newInstance();

      Vertx.clusteredVertx(new VertxOptions())
        .map(Application::new)
        .compose(app -> app.run(args)
          .onSuccess(x -> app.deployNode(node)));
    }
  }
}
