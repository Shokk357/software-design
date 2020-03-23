package akka.search;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchClientStub implements SearchClient {
    private static final int NUMBER_OF_ITEM_IN_REQUEST = 5;
    private final long delay;

    public SearchClientStub(long delay) {
        this.delay = delay;
    }

    public static int getNumberOfItemInRequest() {
        return NUMBER_OF_ITEM_IN_REQUEST;
    }

    @Override
    public SearchResult search(String searchRequest, SearchEngine searchEngine) {
        final List<SearchResultItem> response = IntStream.range(0, NUMBER_OF_ITEM_IN_REQUEST)
                .mapToObj(i -> new SearchResultItem(
                        generateUrl(i),
                        generateTitle(i),
                        generateText(i)
                )).collect(Collectors.toList());

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new SearchResult(response, searchEngine);
    }

    private static String generateUrl(final int index) {
        return String.format("http://url_number_%1$d/some/path/number/%1$d", index);
    }

    private static String generateTitle(final int index) {
        return "Response title #" + index;
    }

    private static String generateText(final int index) {
        return "Text in response #" + index;
    }
}
