package servers;

import dao.MongoFitnessCenterDao;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static utils.HttpRequestUtils.*;

public class HttpManagerServer implements FitnessCenterServer {
    private final MongoFitnessCenterDao dao;

    public HttpManagerServer(MongoFitnessCenterDao dao) {
        this.dao = dao;
    }

    @Override
    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        if (path.equals("create_subscription")) {
            return createSubscription(request.getQueryParameters());
        }
        if (path.equals("renew_subscription")) {
            return renewSubscription(request.getQueryParameters());
        }
        if (path.equals("get_subscription")) {
            return getSubscription(request.getQueryParameters());
        }
        return Observable.just("Unsupported request : " + path);
    }

    Observable<String> createSubscription(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");
        LocalDateTime subscriptionEnd = getLocalDateTimeParam(params, "subscription_end");

        return dao
                .createSubscription(id, subscriptionEnd)
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage);
    }

    Observable<String> renewSubscription(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");
        LocalDateTime subscriptionEnd = getLocalDateTimeParam(params, "subscription_end");

        return dao.renewSubscription(id, subscriptionEnd)
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage);
    }

    Observable<String> getSubscription(Map<String, List<String>> params) {
        long id = getLongParam(params, "id");
        return dao
                .getLastVersionSubscription(id)
                .map(Objects::toString);
    }
}
