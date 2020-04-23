import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import dao.MongoFitnessCenterDao;
import io.reactivex.netty.protocol.http.server.HttpServer;
import org.bson.Document;
import rx.Observable;
import servers.FitnessCenterServer;
import servers.HttpManagerServer;
import servers.HttpReportServer;
import servers.HttpTurnstileServer;

public class Main {
    public static void main(String[] args) {
        MongoFitnessCenterDao dao = createDao();
        HttpManagerServer manager = new HttpManagerServer(dao);
        HttpReportServer report = new HttpReportServer(dao);
        HttpTurnstileServer turnstile = new HttpTurnstileServer(dao);

        new Thread(getServerRunnable(8080, manager)).start();
        new Thread(getServerRunnable(8081, report)).start();
        new Thread(getServerRunnable(8082, turnstile)).start();
    }

    private static Runnable getServerRunnable(int port, FitnessCenterServer server) {
        return () -> HttpServer
                .newServer(port)
                .start((req, resp) -> {
                    Observable<String> response = server.getResponse(req);
                    return resp.writeString(response.map(r -> r + System.lineSeparator()));
                })
                .awaitShutdown();
    }

    private static MongoFitnessCenterDao createDao() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("fitness-center");
        MongoCollection<Document> subscriptions = database.getCollection("subscriptions");
        MongoCollection<Document> events = database.getCollection("events");
        return new MongoFitnessCenterDao(subscriptions, events);
    }
}
