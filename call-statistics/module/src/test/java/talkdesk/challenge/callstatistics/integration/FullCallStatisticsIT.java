package talkdesk.challenge.callstatistics.integration;

import org.junit.jupiter.api.Disabled;

import java.net.URL;

@Disabled
public class FullCallStatisticsIT extends CallStatisticsIT {
  @Override
  protected String[] programArgs() {
    URL mongoConfigUrl = getClass().getClassLoader()
      .getResource("additional-config/external.json");

    return new String[] { mongoConfigUrl.getPath() };
  }
}
