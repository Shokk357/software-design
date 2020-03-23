package akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actors.MasterSearchActor;
import akka.search.SearchClientStub;
import akka.search.SearchEngine;
import akka.search.SearchResult;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static org.junit.Assert.assertEquals;

public class MasterSearchActorTest {
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
                final Props props = Props.create(MasterSearchActor.class, 1, new SearchClientStub(0));
                final ActorRef master = system.actorOf(props);

                within(FiniteDuration.apply(1, TimeUnit.SECONDS),
                        () -> {
                            master.tell("test request", getRef());
                            expectMsgClass(List.class);
                            return null;
                        });
            }
        };
    }

    @Test
    public void testTimeout() {
        new TestKit(system) {
            {
                final Props props = Props.create(MasterSearchActor.class, 1, new SearchClientStub(2000));
                final ActorRef master = system.actorOf(props);

                within(FiniteDuration.apply(4, TimeUnit.SECONDS),
                        () -> {
                            master.tell("test request", getRef());
                            expectMsg(Collections.emptyList());
                            return null;
                        });
            }
        };
    }

    @Test
    public void testLength() {
        new TestKit(system) {
            {
                final Props props = Props.create(MasterSearchActor.class, 1, new SearchClientStub(0));
                final ActorRef master = system.actorOf(props);

                within(FiniteDuration.apply(1, TimeUnit.SECONDS),
                        () -> {
                            final Object response = ask(master, "test length", new Timeout(1, TimeUnit.MINUTES))
                                    .toCompletableFuture()
                                    .join();

                            assertEquals(EnumSet.allOf(SearchEngine.class).size(),
                                    ((List<SearchResult>) response).size());
                            return null;
                        });
            }
        };
    }
}








