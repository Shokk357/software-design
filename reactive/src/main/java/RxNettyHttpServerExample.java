import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

import java.util.*;

public class RxNettyHttpServerExample {

    public static void main(final String[] args) {
        HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    Observable<String> response;
                    String name = req.getDecodedPath().substring(1);
                    switch (name) {
                        case "addUser":
                            response = addUser(req.getQueryParameters());
                            resp.setStatus(HttpResponseStatus.OK);
                            break;
                        case "addProduct":
                            response = addProduct(req.getQueryParameters());
                            resp.setStatus(HttpResponseStatus.OK);
                            break;
                        case "getProducts":
                            response = getProductsByUserId(req.getQueryParameters());
                            resp.setStatus(HttpResponseStatus.OK);
                            break;
                        default:
                            response = Observable.just("Unknown command");
                            resp.setStatus(HttpResponseStatus.BAD_REQUEST);
                    }

                    return resp.writeString(response.concatWith(Observable.just("\n")));
                })
                .awaitShutdown();
    }

    private static String checkAndFormError(Map<String, List<String>> queryParam, List<String> needValues) {
        return needValues.stream()
                .filter(param -> !queryParam.containsKey(param))
                .reduce((s1, s2) -> s1 + ", " + s2)
                .map(s -> "Can't find: " + s)
                .orElse("");
    }

    private static Observable<String> addProduct(Map<String, List<String>> queryParam) {
        ArrayList<String> needValues = new ArrayList<>(Arrays.asList("id", "name", "eur", "dol", "rub"));
        String error = checkAndFormError(queryParam, needValues);
        if (!error.equals("")) {
            return Observable.just(error);
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));

        String name = queryParam.get("name").get(0);

        String eur = queryParam.get("eur").get(0);
        String rub = queryParam.get("rub").get(0);
        String dol = queryParam.get("dol").get(0);

        return ReactiveMongoDriverExample.createProduct(new Product(id, name, rub, eur, dol))
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage);
    }

    private static Observable<String> addUser(Map<String, List<String>> queryParam) {
        ArrayList<String> needValues = new ArrayList<>(Arrays.asList("id", "currency", "name"));
        String error = checkAndFormError(queryParam, needValues);
        if (!error.equals("")) {
            return Observable.just(error);
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));

        String name = queryParam.get("name").get(0);
        String currency = queryParam.get("currency").get(0);
        return ReactiveMongoDriverExample.createUser(new User(id, name, currency))
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage);
    }

    private static Observable<String> getProductsByUserId(Map<String, List<String>> queryParam) {
        ArrayList<String> needValues = new ArrayList<>(Collections.singletonList("id"));
        String error = checkAndFormError(queryParam, needValues);
        if (!error.equals("")) {
            return Observable.just(error);
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        return ReactiveMongoDriverExample.getProductsByUserId(id);
    }
}
