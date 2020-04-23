package servers;

import dao.MongoFitnessCenterDao;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.EventType;
import model.TurnstileEvent;
import rx.Observable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static utils.HttpRequestUtils.getLocalDateTimeParam;
import static utils.HttpRequestUtils.getLongParam;

public class HttpTurnstileServer implements FitnessCenterServer {
    private final MongoFitnessCenterDao dao;
    private final Map<Long, LocalDateTime> subscriptionEnterTime = new HashMap<>();

    public HttpTurnstileServer(MongoFitnessCenterDao dao) {
        this.dao = dao;
    }

    @Override
    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        if (path.equals("exit")) {
            return exit(request.getQueryParameters());
        }
        if (path.equals("enter")) {
            return enter(request.getQueryParameters());
        }
        return Observable.just("Unsupported request : " + path);
    }

    Observable<String> exit(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");
        LocalDateTime timestamp = getLocalDateTimeParam(params, "timestamp");

        if (!subscriptionEnterTime.containsKey(id)) {
            return Observable.just("You aren't in fitness center now");
        }
        LocalDateTime enterTime = subscriptionEnterTime.get(id);
        if (enterTime.isAfter(timestamp)) {
            return Observable.just("Invalid timestamp - you try to exit before enter");
        }
        sendVisit(id, Duration.between(enterTime, timestamp));
        subscriptionEnterTime.remove(id);

        return dao
                .addEvent(new TurnstileEvent(id, EventType.EXIT, timestamp))
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage);
    }

    private void sendVisit(long id, Duration duration) {
        String request = "/add_visit?id=" + id + "&duration=" + duration.toString();
        HttpClient
                .newClient("localhost", 8081)
                .createGet(request)
                .subscribe();
    }

    Observable<String> enter(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");
        LocalDateTime timestamp = getLocalDateTimeParam(params, "timestamp");

        if (subscriptionEnterTime.containsKey(id)) {
            return Observable.just("You are in fitness center now");
        }
        subscriptionEnterTime.put(id, timestamp);

        return dao
                .addEvent(new TurnstileEvent(id, EventType.ENTER, timestamp))
                .map(Objects::toString)
                .onErrorReturn(error -> {
                    subscriptionEnterTime.remove(id);
                    return error.getMessage();
                });
    }
}
