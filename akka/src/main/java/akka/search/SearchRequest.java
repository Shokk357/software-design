package akka.search;

public class SearchRequest {
    private final String requestText;
    private final SearchEngine searchEngine;

    public SearchRequest(String requestText, SearchEngine searchEngine) {
        this.requestText = requestText;
        this.searchEngine = searchEngine;
    }

    public String getRequestText() {
        return requestText;
    }

    public SearchEngine getSearchEngine() {
        return searchEngine;
    }
}
