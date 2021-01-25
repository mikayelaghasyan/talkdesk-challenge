package talkdesk.challenge.callmanagement.integration;

import org.junit.jupiter.api.Disabled;

import java.net.URL;

@Disabled
public class FullCallManagementIT extends CallManagementIT {
  @Override
  protected String[] programArgs() {
    URL mongoConfigUrl = getClass().getClassLoader()
      .getResource("additional-config/mongo.json");

    return new String[] { mongoConfigUrl.getPath() };
  }
}
