import org.bson.Document;

/**
 * @author akirakozov
 */
public class User {
    public final int id;
    public final String name;
    public final String currency;

    public User(int id, String name, String currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public Document getDocument() {
        return new Document("id", id).append("name", name).append("currency", currency);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
