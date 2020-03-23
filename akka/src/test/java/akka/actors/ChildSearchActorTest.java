package akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.search.SearchClientStub;
import akka.search.SearchEngine;
import akka.search.SearchRequest;
import akka.search.SearchResult;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static org.junit.Assert.assertEquals;

public class ChildSearchActorTest {
    private static ActorSystem system;

    @BeforeAll
    public static void setUp() throws Exception {
        system = ActorSystem.create("TestSystem");
    }

    @AfterAll
    public static void tearDown() throws Exception {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testIt() {
        new TestKit(system) {
            {
                final Props props = Props.create(ChildSearchActor.class, new SearchClientStub(0));
                final ActorRef child = system.actorOf(props);

                within(FiniteDuration.apply(1, TimeUnit.SECONDS),
                        () -> {
                            final Object response = ask(child, new SearchRequest("test request", SearchEngine.ЯНДЕКС), new Timeout(1, TimeUnit.MINUTES))
                                    .toCompletableFuture()
                                    .join();

                            assertEquals(SearchClientStub.getNumberOfItemInRequest(),
                                    ((SearchResult) response).getSearchResult().size());
                            return null;
                        });
            }
        };
    }
}