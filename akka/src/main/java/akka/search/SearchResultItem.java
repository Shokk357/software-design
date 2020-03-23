package akka.search;

public class SearchResultItem {
    private final String url;
    private final String title;
    private final String text;

    public SearchResultItem(String url, String title, String text) {
        this.url = url;
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return "SearchResult{\n" +
                "\t\t\"url\": \"" + url + "\",\n" +
                "\t\t\"title\": \"" + title + "\",\n" +
                "\t\t\"text\": \"" + text + "\"\n" +
                "\t}";
    }
}
