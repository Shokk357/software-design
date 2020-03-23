package akka.actors;

import akka.actor.UntypedActor;
import akka.search.SearchClient;
import akka.search.SearchRequest;

public class ChildSearchActor extends UntypedActor {
    private final SearchClient searchClient;

    public ChildSearchActor(SearchClient searchClient) {
        super();
        this.searchClient = searchClient;
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof SearchRequest) {
            final SearchRequest msg = (SearchRequest) o;
            sender().tell(searchClient.search(msg.getRequestText(), msg.getSearchEngine()), self());
        }
    }
}
