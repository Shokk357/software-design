package servers;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

public interface FitnessCenterServer {
    <T> Observable<String> getResponse(HttpServerRequest<T> request);
}
