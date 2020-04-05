import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;

public class ReactiveMongoDriverExample {

    private static MongoClient client = createMongoClient();

    public static Observable<Success> createUser(User user) {
        return client.getDatabase("rxtest").getCollection("user")
                .find(eq("id", user.id))
                .toObservable()
                .isEmpty()
                .flatMap(isEmpty -> {
                    if (isEmpty) {
                        return client.getDatabase("rxtest").getCollection("user").insertOne(user.getDocument());
                    } else {
                        return Observable.error(new IllegalArgumentException("Duplicated id"));
                    }
                });
    }

    public static Observable<Success> createProduct(Product product) {
        return client.getDatabase("rxtest").getCollection("product")
                .find(eq("id", product.id))
                .toObservable()
                .isEmpty()
                .flatMap(isEmpty -> {
                    if (isEmpty) {
                        return client.getDatabase("rxtest").getCollection("product").insertOne(product.getDocument());
                    } else {
                        return Observable.error(new IllegalArgumentException("Duplicated id"));
                    }
                });
    }


    public static Observable<String> getProductsByUserId(int id) {
        return findUserCurrencyById(id)
                .flatMap(cur ->
                        client.getDatabase("rxtest").getCollection("product")
                                .find()
                                .toObservable()
                                .map(d -> new Product(d).toStringByCurrency(cur))
                                .reduce((str1, str2) -> str1 + ", " + str2)
                                .map(products -> "{ id = " + id + ", products = [" + products  + "]}")
                );
    }

    private static Observable<String> findUserCurrencyById(int id) {
        return client.getDatabase("rxtest").getCollection("user")
                .find(eq("id", id))
                .toObservable()
                .map(d -> d.getString("currency"))
                ;
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}

