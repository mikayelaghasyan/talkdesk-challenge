package talkdesk.challenge.core.runtime;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.Arrays;

public class LocalRunner {
  public static void main(String[] args) {
    try {
      new LocalRunner().run(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private void run(String[] args) throws Exception {
    String nodeClassName = args[0];
    String[] configArgs = Arrays.copyOfRange(args, 1, args.length);
    Class<? extends Node> cls = (Class<? extends Node>) Class.forName(nodeClassName);
    Node node = cls.getConstructor().newInstance();

    Vertx.clusteredVertx(new VertxOptions())
      .map(Application::new)
      .compose(app -> app.run(configArgs)
        .onSuccess(x -> app.deployNode(node)));
  }
}
