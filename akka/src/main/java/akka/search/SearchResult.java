package akka.search;

import java.util.List;

public class SearchResult {

    private final List<SearchResultItem> searchResult;
    private final SearchEngine engine;

    public SearchResult(List<SearchResultItem> searchResult, SearchEngine engine) {
        this.searchResult = searchResult;
        this.engine = engine;
    }


    @Override
    public String toString() {
        return "ResponseResult{\n" +
                "\t\"searchResult\": " + searchResult + ",\n" +
                "\t\"engine\": " + engine + "\n" +
                "}";
    }

    public List<SearchResultItem> getSearchResult() {
        return searchResult;
    }
}
