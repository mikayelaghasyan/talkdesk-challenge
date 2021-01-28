package talkdesk.challenge.callmanagement.integration;

import org.junit.jupiter.api.Disabled;

import java.net.URL;

@Disabled
public class FullCallManagementIT extends CallManagementIT {
  @Override
  protected String[] programArgs() {
    URL externalConfigUrl = getClass().getClassLoader()
      .getResource("additional-config/external.json");

    assert externalConfigUrl != null;
    return new String[] { externalConfigUrl.getPath() };
  }
}
