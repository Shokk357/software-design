package model;

import org.bson.Document;

import java.time.LocalDateTime;

import static utils.LocalDateTimeUtils.FORMATTER;
import static utils.LocalDateTimeUtils.fromString;

public class Subscription implements Comparable<Subscription> {
    private final long id;
    private final LocalDateTime creationTimestamp;
    private final LocalDateTime subscriptionEnd;

    public Subscription(long id, LocalDateTime creationTimestamp, LocalDateTime subscriptionEnd) {
        this.id = id;
        this.creationTimestamp = creationTimestamp;
        this.subscriptionEnd = subscriptionEnd;
    }

    public Subscription(long id, LocalDateTime subscriptionEnd) {
        this(id, LocalDateTime.now(), subscriptionEnd);
    }

    public Subscription(Document document) {
        this(
                document.getLong("id"),
                fromString(document.getString("creationTimestamp")),
                fromString(document.getString("subscriptionEnd"))
        );
    }

    public LocalDateTime getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public long getId() {
        return id;
    }

    public Document toDocument() {
        return new Document()
                .append("id", id)
                .append("creationTimestamp", creationTimestamp.format(FORMATTER))
                .append("subscriptionEnd", subscriptionEnd.format(FORMATTER));
    }

    @Override
    public String toString() {
        return "Subscription {\n" +
                "  id : " + id + ",\n" +
                "  creationTimesamp : " + creationTimestamp.format(FORMATTER) + ",\n" +
                "  subscriptionEnd : " + subscriptionEnd.format(FORMATTER) + "\n" +
                "}\n";
    }

    @Override
    public int compareTo(Subscription other) {
        return creationTimestamp.compareTo(other.creationTimestamp);
    }
}
