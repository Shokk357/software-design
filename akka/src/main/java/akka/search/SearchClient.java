package akka.search;

public interface SearchClient {
    SearchResult search(String searchRequest, SearchEngine searchEngine);
}
