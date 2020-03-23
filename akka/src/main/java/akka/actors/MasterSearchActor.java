package akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.search.*;
import akka.util.Timeout;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.PatternsCS.ask;


public class MasterSearchActor extends UntypedActor {
    private static final Set<SearchEngine> ENGINES = EnumSet.allOf(SearchEngine.class);

    private int receiveTimeout;
    private final SearchClient client;

    private List<SearchResult> responseList = new ArrayList<>();
    private int child_number = 0;

    public MasterSearchActor(int receiveTimeout, SearchClient client) {
        this.receiveTimeout = receiveTimeout;
        this.client = client;
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof String) {
            final String searchRequest = (String) o;
            List<CompletableFuture<Object>> futures = new ArrayList<>();

            ENGINES.forEach(engine -> {
                String name = "child" + child_number++;

                final ActorRef child = getContext().actorOf(
                        Props.create(ChildSearchActor.class, client),
                        name);

                try {
                    futures.add(ask(child, new SearchRequest(searchRequest, engine), new Timeout(receiveTimeout, TimeUnit.SECONDS)).toCompletableFuture());
                } catch (Exception ignored) {
                }
            });
            futures.forEach(future -> {
                Object response = null;
                try {
                    response = future.get(receiveTimeout, TimeUnit.SECONDS);
                } catch (TimeoutException | InterruptedException | ExecutionException ignored) {
                }
                if (response != null) {
                    responseList.add((SearchResult) response);
                }
            });

            sender().tell(responseList, self());
        }
    }
}
