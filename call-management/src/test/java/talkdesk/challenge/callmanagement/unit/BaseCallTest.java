package talkdesk.challenge.callmanagement.unit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Future;
import io.vertx.core.json.jackson.DatabindCodec;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import talkdesk.challenge.callmanagement.model.Call;
import talkdesk.challenge.core.communication.CommandContext;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.db.ReadOnlyRepository;
import talkdesk.challenge.core.db.ReadWriteRepository;
import talkdesk.challenge.core.domainevent.DomainEventBus;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public abstract class BaseCallTest {
  @Mock
  protected DomainEventBus eventBus;

  @Mock
  protected CommunicationBus communicationBus;

  @Mock
  protected ReadOnlyRepository<Call> readOnlyCallRepository;

  @Mock
  protected ReadWriteRepository<Call> readWriteCallRepository;

  protected QueryContext queryContext;
  protected CommandContext commandContext;

  @BeforeEach
  void baseSetUp() {
    configureCodec();

    queryContext = spy(new QueryContext(communicationBus, null));
    commandContext = spy(new CommandContext(communicationBus, eventBus, null));
    stubPublish();
    stubRepositories();
  }

  private void configureCodec() {
    Stream.of(DatabindCodec.mapper(), DatabindCodec.prettyMapper())
      .forEach(mapper -> {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new JavaTimeModule());
      });
  }

  private void stubPublish() {
    lenient().when(eventBus.publish(eq("call"), any()))
      .thenAnswer(x -> Future.succeededFuture());
  }

  private void stubRepositories() {
    lenient().when(queryContext.repositoryOf("call", Call.class))
      .thenAnswer(x -> readOnlyCallRepository);
    lenient().when(commandContext.repositoryOf("call", Call.class))
      .thenAnswer(x -> readWriteCallRepository);
    lenient().when(readWriteCallRepository.save(any()))
      .thenAnswer(x -> Future.succeededFuture());
  }
}
