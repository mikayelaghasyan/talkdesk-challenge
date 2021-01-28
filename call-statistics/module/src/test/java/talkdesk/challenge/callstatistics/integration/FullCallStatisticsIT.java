package talkdesk.challenge.callstatistics.integration;

import org.junit.jupiter.api.Disabled;

import java.net.URL;

@Disabled
public class FullCallStatisticsIT extends CallStatisticsIT {
  @Override
  protected String[] programArgs() {
    URL externalConfigUrl = getClass().getClassLoader()
      .getResource("additional-config/external.json");

    assert externalConfigUrl != null;
    return new String[] { externalConfigUrl.getPath() };
  }
}
